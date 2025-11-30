package amov.a2020157100.ecomap.ui.viewmodels

import amov.a2020157100.ecomap.model.Condition
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.model.toUser
import amov.a2020157100.ecomap.model.User
import amov.a2020157100.ecomap.utils.camera.FileUtils
import amov.a2020157100.ecomap.utils.firebase.FAuthUtil
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import android.location.Location
import kotlinx.coroutines.launch
import amov.a2020157100.ecomap.utils.firebase.FStorageUtil
import amov.a2020157100.ecomap.utils.text.UiText
import amov.a2020157100.ecomap.R
import androidx.compose.runtime.mutableDoubleStateOf
import kotlinx.coroutines.future.await
import kotlin.collections.orEmpty
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class FirebaseViewModel : ViewModel() {

    // Variaveis
    var loginEmail = mutableStateOf("")
    var loginPassword = mutableStateOf("")
    var registerEmail = mutableStateOf("")
    var registerPassword = mutableStateOf("")
    var registerConfirmPassword = mutableStateOf("")
    var selectedFilter = mutableStateOf("All")
    var reportState = mutableStateOf("")
    var reportNotes = mutableStateOf("")
    var addReportPhotoPath = mutableStateOf<String?>(null)
    var addType = mutableStateOf("")
    var addLatitude = mutableDoubleStateOf(0.0)
    var addLongitude = mutableDoubleStateOf(0.0)
    var addNotes = mutableStateOf("")
    var addPhotoPath = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    private val SEARCH_RADIUS_METERS = 3000.0

    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user: State<User?> get() = _user

    private val _recyclingPoints = mutableStateOf<List<RecyclingPoint>>(emptyList())
    val recyclingPoints: State<List<RecyclingPoint>> get() = _recyclingPoints

    private val _error = mutableStateOf<UiText.StringResource?>(null)
    val error: State<UiText.StringResource?> get() = _error

    private val _sucess = mutableStateOf<String?>(null)
    val sucess: State<String?> get() = _sucess

    private val _selectedRecyclingPoint = mutableStateOf<RecyclingPoint?>(null)
    val selectedRecyclingPoint: State<RecyclingPoint?> get() = _selectedRecyclingPoint


    // --- FUNÇÕES AUXILIARES DE FORMULÁRIO ---
    fun resetReportState() {
        reportState.value = ""
        reportNotes.value = ""
        addReportPhotoPath.value = null
    }

    fun resetAddForm() {
        addType.value = ""
        addLatitude.value = 0.0
        addLongitude.value = 0.0
        addNotes.value = ""
        addPhotoPath.value = null
    }

    fun resetRegisterForm(){
        registerEmail.value = ""
        registerPassword.value = ""
        registerConfirmPassword.value = ""
    }

    fun resetLoginForm(){
        loginEmail.value = ""
        loginPassword.value = ""

    }
    fun clearError() {
        _error.value = null
    }

    //autenticação

    fun createUserWithEmail(email: String, password: String, passwordConfirm: String) {
        _error.value = null
        if (email.isBlank() || password.isBlank() || passwordConfirm.isBlank()) return
        if (password != passwordConfirm) {
            _error.value =  UiText.StringResource(R.string.passwords_not_match)
            return
        }
        viewModelScope.launch {
            FAuthUtil.createUserWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = handleAuthException(exception)
            }
        }
    }

    fun signInWithEmail(email: String, password: String){
        _error.value = null
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = handleAuthException(exception)
            }
        }
    }

    fun signOut() {
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    private fun handleAuthException(exception: Throwable?): UiText.StringResource? {
        if(exception == null) return null
        return when(exception){
            is FirebaseAuthWeakPasswordException -> UiText.StringResource(R.string.error_auth_weak_password)
            is FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.error_auth_email_in_use)
            is FirebaseAuthInvalidCredentialsException -> {
                when(exception.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> UiText.StringResource(R.string.error_auth_wrong_password)
                    "ERROR_INVALID_EMAIL" -> UiText.StringResource(R.string.error_auth_invalid_email)
                    else -> UiText.StringResource(R.string.error_auth_generic)
                }
            }
            is FirebaseAuthInvalidUserException -> {
                when(exception.errorCode) {
                    "ERROR_USER_NOT_FOUND" ->UiText.StringResource(R.string.error_auth_user_not_found)
                    "ERROR_INVALID_EMAIL" -> UiText.StringResource(R.string.error_auth_invalid_email)
                    else -> UiText.StringResource(R.string.error_auth_generic)
                }
            }
            else -> UiText.StringResource(R.string.error_auth_generic)
        }


    }

    // logica

    fun addRecyclingPoint(
        type: String,
        latitude: Double,
        longitude: Double,
        imgPath: String?,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        _error.value = null
        isLoading.value = true

        if (type.isBlank() || latitude == 0.0 || longitude == 0.0) {
            _error.value =  UiText.StringResource(R.string.error_mandatory_fields)
            isLoading.value = false
            return
        }

        viewModelScope.launch {
            var downloadUrl: String? = null
            if (imgPath != null) {
                try {
                    val compressedPath = FileUtils.compressImage(imgPath)
                    downloadUrl = FStorageUtil.uploadFile(compressedPath).await()
                } catch (e: Exception) {
                    _error.value = UiText.StringResource(R.string.error_compress_image)
                    isLoading.value = false
                    return@launch
                }
            }

            val user = _user.value
            if (user == null) {
                isLoading.value = false
                return@launch
            }

            val success = FStorageUtil.addRecyclingPoint(
                user.uid, type, latitude, longitude, downloadUrl, notes
            )

            if (!success) {
                _error.value = UiText.StringResource(R.string.error_add_ecoponto_failed)
            } else {
                getRecyclingPoints()
                onSuccess()
            }
            isLoading.value = false
        }
    }

    fun getRecyclingPoints(userLocation: Location? = null){
        _error.value = null
        isLoading.value = true
        viewModelScope.launch {

            val allPoints = FStorageUtil.getRecyclingPoints()
            if (userLocation != null && allPoints.isNotEmpty()) {
                    val sortedAndFilteredPoints = allPoints.filter { point ->

                        val pointLocation = Location("point").apply {
                            latitude = point.latatitude
                            longitude = point.longitude
                        }
                        userLocation.distanceTo(pointLocation) <= SEARCH_RADIUS_METERS
                    }.sortedBy { point ->
                        val pointLocation = Location("point").apply {
                            latitude = point.latatitude
                            longitude = point.longitude
                        }
                        userLocation.distanceTo(pointLocation)
                    }

                    _recyclingPoints.value = sortedAndFilteredPoints
                    isLoading.value = false
                }

            isLoading.value = false

        }
    }

    fun getRecyclingPoint(recyclingPointId: String) {
        _error.value = null
        isLoading.value = true
        viewModelScope.launch {
            _selectedRecyclingPoint.value = null
            FStorageUtil.getRecyclingPoint(recyclingPointId) { recyclingPoint ->
                if (recyclingPoint != null) {
                    isLoading.value = false
                    _selectedRecyclingPoint.value = recyclingPoint
                } else {
                    isLoading.value = false
                    _error.value = UiText.StringResource(R.string.error_add_ecoponto_failed)
                }
            }
        }
    }


    fun confirmEcoponto(recyclingPointId: String) {
        _error.value = null
        isLoading.value = true

        viewModelScope.launch {
            val user = _user.value
            if (user == null) {
                isLoading.value = false
                return@launch
            }

            val selectedPoint = _selectedRecyclingPoint.value

            if (selectedPoint != null && selectedPoint.id == recyclingPointId) {

                if (selectedPoint.creator == user.uid) {
                        _error.value = UiText.StringResource(R.string.error_creator_cannot_vote)
                    isLoading.value = false
                        return@launch
                }

                if (selectedPoint.idsVoteAprove.orEmpty().contains(user.uid)) {
                        _error.value =  UiText.StringResource(R.string.error_already_voted_confirm)
                        isLoading.value = false
                        return@launch
                }

                if (selectedPoint.status == Status.FINAL.name) {
                        _error.value = UiText.StringResource(R.string.error_already_verified)
                        isLoading.value = false
                        return@launch
                }

                FStorageUtil.confirmRecyclingPoint(recyclingPointId, user.uid)
                getRecyclingPoint(recyclingPointId)
                isLoading.value = false
            }
        }

    }

    fun deleteEcoponto(recyclingPointId: String){
        _error.value = null
        isLoading.value = true
            viewModelScope.launch {
                val user = _user.value
                if (user == null) {
                    isLoading.value = false
                    return@launch
                }
                val selectedPoint = _selectedRecyclingPoint.value

                if (selectedPoint != null && selectedPoint.id == recyclingPointId) {

                    if (selectedPoint.idsVoteRemove.orEmpty().contains(user.uid)) {
                        _error.value = UiText.StringResource(R.string.error_already_voted_delete)
                        isLoading.value = false
                        return@launch
                    }

                    FStorageUtil.deleteRecyclingPoint(recyclingPointId, user.uid)
                    getRecyclingPoint(recyclingPointId)
                    isLoading.value = false
                }
            }

    }

    fun updateEcopontoCondicion(
        recyclingPointId: String,
        state: String,
        notes: String?,
        imgUrl: String?
    ) {
        _error.value = null
        isLoading.value = true
        viewModelScope.launch {
            var downloadUrl: String? = null
            if (imgUrl != null) {
                try {
                    val compressedPath = FileUtils.compressImage(imgUrl)
                    downloadUrl = FStorageUtil.uploadFile(compressedPath).await()
                } catch (e: Exception) {
                    _error.value = UiText.StringResource(R.string.error_compress_image)
                    isLoading.value = false
                    return@launch
                }
            }
            val user = _user.value
            if (user == null) {
                isLoading.value = false
                return@launch
            }

            val condition = Condition(user.uid, state, notes, downloadUrl)

            FStorageUtil.updateCondition(recyclingPointId, condition)
            getRecyclingPoint(recyclingPointId)
            resetReportState()
            isLoading.value = false
        }
    }

}
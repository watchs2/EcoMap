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
import android.util.Log
import kotlinx.coroutines.future.await
import kotlin.collections.orEmpty

class FirebaseViewModel : ViewModel() {

    // --- VARIÁVEIS DE ESTADO (Mantidas iguais) ---
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
    var addLatitude = mutableStateOf(0.0)
    var addLongitude = mutableStateOf(0.0)
    var addNotes = mutableStateOf("")
    var addPhotoPath = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    private val SEARCH_RADIUS_METERS = 3000.0

    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user: State<User?> get() = _user

    private val _recyclingPoints = mutableStateOf<List<RecyclingPoint>>(emptyList())
    val recyclingPoints: State<List<RecyclingPoint>> get() = _recyclingPoints

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

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

    // --- AUTENTICAÇÃO ---
    fun createUserWithEmail(email: String, password: String, passwordConfirm: String) {
        _error.value = null
        if (email.isBlank() || password.isBlank() || passwordConfirm.isBlank()) return
        if (password != passwordConfirm) {
            _error.value = "Passwords do not match"
            return
        }
        viewModelScope.launch {
            FAuthUtil.createUserWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _error.value = null
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signOut() {
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    // --- STORAGE & LOGIC ---

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
            _error.value = "Preencha todos os campos obrigatórios"
            isLoading.value = false // Importante parar o loading
            return
        }

        viewModelScope.launch {
            var downloadUrl: String? = null
            if (imgPath != null) {
                try {
                    val compressedPath = FileUtils.compressImage(imgPath)
                    downloadUrl = FStorageUtil.uploadFile(compressedPath).await()
                } catch (e: Exception) {
                    _error.value = "Erro ao enviar imagem ${e.message}"
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
                _error.value = "Erro ao adicionar ponto de reciclagem"
            } else {
                getRecyclingPoints()
                onSuccess()
            }
            isLoading.value = false
        }
    }

    fun getRecyclingPoints(userLocation: Location? = null) {
        _error.value = null
        viewModelScope.launch {
            // 1. Obter todos os pontos do Firebase
            val allPoints = FStorageUtil.getRecyclingPoints()

            if (allPoints != null) {
                if (userLocation != null) {
                    // 2. Se temos localização, filtrar e ordenar
                    val sortedAndFilteredPoints = allPoints.filter { point ->
                        // Criar objeto Location para o ponto (nota o erro ortográfico 'latatitude' que vem do modelo)
                        val pointLocation = Location("point").apply {
                            latitude = point.latatitude
                            longitude = point.longitude
                        }
                        // Filtrar: Manter apenas se a distância for menor que o raio
                        userLocation.distanceTo(pointLocation) <= SEARCH_RADIUS_METERS
                    }.sortedBy { point ->
                        // Ordenar: Do mais perto para o mais longe
                        val pointLocation = Location("point").apply {
                            latitude = point.latatitude
                            longitude = point.longitude
                        }
                        userLocation.distanceTo(pointLocation)
                    }

                    _recyclingPoints.value = sortedAndFilteredPoints
                } else {
                    // Se não há localização, mostra todos (comportamento original)
                    _recyclingPoints.value = allPoints
                }
            } else {
                _error.value = "Erro ao carregar lista"
            }
        }
    }

    fun getRecyclingPoint(recyclingPointId: String) {
        _error.value = null
        viewModelScope.launch {
            _selectedRecyclingPoint.value = null
            FStorageUtil.getRecyclingPoint(recyclingPointId) { recyclingPoint ->
                if (recyclingPoint != null) {
                    _selectedRecyclingPoint.value = recyclingPoint
                } else {
                    _error.value = "Falha ao carregar detalhes do Ecoponto"
                }
            }
        }
    }

    fun clearSelectedRecyclingPoint() {
        _selectedRecyclingPoint.value = null
    }

    // --- CORREÇÕES A PARTIR DAQUI ---

    fun confirmEcoponto(recyclingPointId: String) {
        _error.value = null

        _user.value?.let { user ->
            viewModelScope.launch {
                val selectedPoint = _selectedRecyclingPoint.value

                if (selectedPoint != null && selectedPoint.id == recyclingPointId) {

                    if (selectedPoint.creator == user.uid) {
                        _error.value = "O criador não pode votar para confirmar"
                        return@launch // IMPORTANTE: Parar a execução
                    }

                    if (selectedPoint.idsVoteAprove.orEmpty().contains(user.uid)) {
                        _error.value = "Já votaste para confirmar este ecoponto."
                        return@launch // IMPORTANTE: Parar a execução
                    }

                    if (selectedPoint.status == Status.FINAL.name) {
                        _error.value = "Este ecoponto já está verificado."
                        return@launch // IMPORTANTE: Parar a execução
                    }

                    FStorageUtil.confirmRecyclingPoint(recyclingPointId, user.uid)
                    getRecyclingPoint(recyclingPointId) // Atualizar a UI
                }
            }
        }
    }

    fun deleteEcoponto(recyclingPointId: String) {
        _error.value = null
        _user.value?.let { user ->
            viewModelScope.launch {
                val selectedPoint = _selectedRecyclingPoint.value

                if (selectedPoint != null && selectedPoint.id == recyclingPointId) {

                    if (selectedPoint.idsVoteRemove.orEmpty().contains(user.uid)) {
                        _error.value = "Já votaste para eliminar este ecoponto."
                        return@launch // IMPORTANTE: Parar a execução
                    }

                    FStorageUtil.deleteRecyclingPoint(recyclingPointId, user.uid)
                    getRecyclingPoint(recyclingPointId)
                }
            }
        }
    }

    fun updateEcopontoCondicion(
        recyclingPointId: String,
        state: String,
        notes: String?,
        imgUrl: String?
    ) {
        isLoading.value = true
        viewModelScope.launch {
            var downloadUrl: String? = null

            // Se houver imagem, faz upload primeiro
            if (imgUrl != null) {
                try {
                    val compressedPath = FileUtils.compressImage(imgUrl)
                    downloadUrl = FStorageUtil.uploadFile(compressedPath).await()
                } catch (e: Exception) {
                    _error.value = "Erro ao enviar imagem ${e.message}"
                    return@launch
                }
            }

            val user = _user.value ?: return@launch
            val condition = Condition(user.uid, state, notes, downloadUrl)

            FStorageUtil.updateCondition(recyclingPointId, condition)
            getRecyclingPoint(recyclingPointId)
            isLoading.value = false
        }
    }

}
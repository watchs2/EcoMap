package amov.a2020157100.ecomap.ui.viewmodels

import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.toUser
import amov.a2020157100.ecomap.model.User
import amov.a2020157100.ecomap.utils.firebase.FAuthUtil
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import amov.a2020157100.ecomap.utils.firebase.FStorageUtil
import kotlin.collections.mutableListOf

class FirebaseViewModel : ViewModel() {
    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user: State<User?>
        get() = _user

    private val _recyclingPoints = mutableStateOf<List<RecyclingPoint>>(emptyList())
    val recyclingPoints: State<List<RecyclingPoint>>
        get() = _recyclingPoints

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?>
        get() = _error

    fun createUserWithEmail(email: String, password: String, passwordConfirm: String){
        _error.value = null
        if(email.isBlank() || password.isBlank() || passwordConfirm.isBlank()){
            return
        }
        if(password != passwordConfirm){
            _error.value = "Passwords do not match"
            return
        }
        viewModelScope.launch {
            FAuthUtil.createUserWithEmail(email,password) { exception ->
                if(exception == null){
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }

    }

    fun signInWithEmail(email: String,password: String){
        _error.value = null
        if(email.isBlank() || password.isBlank()){
            return
        }
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email,password){ exception ->
                if(exception == null){
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signOut(){
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    //Storage

    fun addRecyclingPoint(
        type: String,
        latatitude: Double,
        longitude: Double,
        imgUrl: String?,
        notes: String?
    ){
        _error.value = null
        //validar dados
        if(type == "" || latatitude == 0.0 || longitude == 0.0 ){
            _error.value = "Prencha todos os campos obrigatórios"
            return
        }

        _user.value?.let { user ->
            viewModelScope.launch {
                FStorageUtil.addRecyclingPoint(
                    user.uid,
                    type,
                    latatitude,
                    longitude,
                    imgUrl,
                    notes
                )
            }
        }
    }

    fun getRecyclingPoints(){
        viewModelScope.launch {
            FStorageUtil.getRecyclingPoints()
        }
    }




}
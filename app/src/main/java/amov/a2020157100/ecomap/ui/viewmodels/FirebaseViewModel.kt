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
import android.util.Log
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

    //ver se esta é a melhor forma
    private val _selectedRecyclingPoint = mutableStateOf<RecyclingPoint?>(null)
    val selectedRecyclingPoint: State<RecyclingPoint?>
        get() = _selectedRecyclingPoint


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
        notes: String?,
        onSuccess: () -> Unit
    ) {
        _error.value = null

        if (type.isBlank() || latatitude == 0.0 || longitude == 0.0) {
            _error.value = "Preencha todos os campos obrigatórios"
            return
        }

        _user.value?.let { user ->
            viewModelScope.launch {
                val success = FStorageUtil.addRecyclingPoint(
                    user.uid,
                    type,
                    latatitude,
                    longitude,
                    imgUrl,
                    notes
                )

                if (!success) {
                    _error.value = "Erro ao adicionar ponto de reciclagem"
                }else{
                    getRecyclingPoints()
                    onSuccess()
                }
            }
        }
    }

    fun getRecyclingPoints() {
        viewModelScope.launch {
            val recyclingPoints = FStorageUtil.getRecyclingPoints()
            Log.d("FIREBASE", "📥 Resultado recebido: ${recyclingPoints?.size ?: "null"} pontos")
            if(recyclingPoints != null){
                Log.d("FIREBASE", "✅ Dados válidos, a atualizar _recyclingPoints...")
                _recyclingPoints.value = recyclingPoints
            }else{
               //erro ou vazio
                _error.value="Erro"
            }
        }
    }

    fun getRecyclingPoint(recyclingPointId: String) {
        viewModelScope.launch {
            _selectedRecyclingPoint.value = null
            FStorageUtil.getRecyclingPoint(recyclingPointId) { recyclingPoint ->
                if (recyclingPoint != null) {
                    _selectedRecyclingPoint.value = recyclingPoint
                } else {
                    _error.value = "Falha ao carregar detalhes do Ecoponto" // TODO: Mover para strings
                }
            }
        }
    }

    fun clearSelectedRecyclingPoint() {
        _selectedRecyclingPoint.value = null
    }


    fun confirmEcoponto(recyclingPointId: String) {
        _user.value?.let { user ->
            Log.d("FirebaseViewModel", "Utilizador ${user.uid} confirmou o ecoponto $recyclingPointId")
            // Chamar a função stub
            FStorageUtil.confirmRecyclingPoint(recyclingPointId, user.uid)

            // TODO: Adicionar lógica real.
            // Por agora, apenas atualiza o estado local (se necessário) ou recarrega.
            // A lógica de "idsVoteAprove" deve ser tratada no FStorageUtil ou Cloud Function.

            // Exemplo: recarregar os dados
            getRecyclingPoint(recyclingPointId)
        }
    }

    // NOVAS FUNÇÕES (Stubs para a lógica de verificação)
    fun reportEcoponto(recyclingPointId: String) {
        _user.value?.let { user ->
            Log.d("FirebaseViewModel", "Utilizador ${user.uid} reportou o ecoponto $recyclingPointId")
            // Chamar a função stub
            FStorageUtil.deleteRecyclingPoint(recyclingPointId, user.uid)

            // TODO: Adicionar lógica real.

            // Exemplo: recarregar os dados
            getRecyclingPoint(recyclingPointId)
        }
    }






}
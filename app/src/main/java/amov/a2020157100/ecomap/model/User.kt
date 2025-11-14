package amov.a2020157100.ecomap.model
import com.google.firebase.auth.FirebaseUser
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val picture: String?
)

fun FirebaseUser.toUser(): User {
    return User(
        uid = uid,
        name = displayName ?: "",
        email = email ?: "",
        picture = photoUrl?.toString()
    )
}
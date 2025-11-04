package amov.a2020157100.ecomap.model
import com.google.firebase.auth.FirebaseUser
data class User(
    val name: String,
    val email: String,
    val picture: String?
)

fun FirebaseUser.toUser(): User {
    return User(
        name = displayName ?: "",
        email = email ?: "",
        picture = photoUrl?.toString()
    )
}
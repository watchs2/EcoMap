package amov.a2020157100.ecomap.model


enum class Status {
    PENDING,
    FINAL,
    DELETE
}



data class RecyclingPoint(

    val id: String,
    val creator: String,
    val type: String,
    val latatitude: Double,
    val longitude: Double,
    val imgUrl: String?,
    val notes: String?,
    val status: String,

    //Aprovar e Remover
    val idsVoteRemove : List<String>?,
    val idsVoteAprove : List<String>?,

    val condition: Condition?,

)

//cheio, danificado, desaparecido
data class Condition(
    val creator: String,
    val state: String,
    val notes: String?,
    val imgUrl: String?,
)






package amov.a2020157100.ecomap.model

import com.google.android.datatransport.cct.StringMerger

enum class Tipo{
    AZUL,
    VERDE,
    AMARELO,
    VERMELHO,
    INDIFERENCIADO
}

enum class Condicao{
    CHEIO,
    DANIFICADO,
    DESAPARECIDO,
    BOM,
}

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

)



/*
    Tlvz precise de um metodo para converter de fb para este tipo
 */



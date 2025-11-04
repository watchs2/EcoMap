package amov.a2020157100.ecomap.model

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
    DESAPARECIDO
}

enum class Estado{
    TEMPORARIO,
    DEFINITIVO,
    ELIMINAR
}


data class Ecoponto(
    val id: String,

    //localização
    val latitude: Double,
    val longitude: Double,

    //info
    val tipo: Tipo,
    val picture: String?,
    val condicao: Condicao,
    val observacoes: String?,
    val estado: Estado,

    val nConfirmados: Int = 0,
    val nEliminados: Int = 0,

    //Tlvz seja necessario
    val creatorId: String,
    val removerId: String,

    //Lista de quem votou em quem
)
/*
    Tlvz precise de um metodo para converter de fb para este tipo
 */



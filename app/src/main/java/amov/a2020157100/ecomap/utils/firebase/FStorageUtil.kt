package amov.a2020157100.ecomap.utils.firebase

import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.model.RecyclingPoint
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.io.InputStream
import android.content.res.AssetManager
import kotlinx.coroutines.tasks.await


class FStorageUtil {
    companion object {

        suspend fun addRecyclingPoint(
            creator: String,
            type: String,
            latatitude: Double,
            longitude: Double,
            imgUrl: String?,
            notes: String?
        ): Boolean {
            return try {
                val db = Firebase.firestore

                val recyclingPoint = hashMapOf(
                    "creator" to creator,
                    "type" to type,
                    "latatitude" to latatitude,
                    "longitude" to longitude,
                    "imgUrl" to imgUrl,
                    "notes" to notes,
                    "status" to Status.PENDING
                )

                db.collection("RecyclingPoints")
                    .add(recyclingPoint)
                    .await()

                true

            } catch (e: Exception) {
                //erro
                false
            }
        }

        //Todos
        suspend fun getRecyclingPoints(): List<RecyclingPoint> {
            val db = Firebase.firestore

            val result = db.collection("RecyclingPoints")
                .get()
                .await()

            val recyclingPoints = mutableListOf<RecyclingPoint>()

            for (document in result) {
                recyclingPoints.add(
                    RecyclingPoint(
                        id = document.id,
                        creator = document.getString("creator") ?: "",
                        type = document.getString("type") ?: "",
                        latatitude = document.getDouble("latatitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        imgUrl = document.getString("imgUrl"),
                        notes = document.getString("notes"),
                        status = document.getString("status") ?: "",
                        idsVoteRemove = document.get("idsVoteRemove") as? List<String>,
                        idsVoteAprove = document.get("idsVoteAprove") as? List<String>
                    )
                )
            }

            return recyclingPoints
        }


        //get de um especifico ecoponto
        fun getRecyclingPoint(recyclingPointId: String, onResult: (RecyclingPoint?) -> Unit) {
            val db = Firebase.firestore

            db.collection("RecyclingPoints")
                .document(recyclingPointId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val recyclingPoint = RecyclingPoint(
                            id = document.id,
                            creator = document.getString("creator") ?: "",
                            type = document.getString("type") ?: "",
                            latatitude = document.getDouble("latatitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            imgUrl = document.getString("imgUrl"),
                            notes = document.getString("notes"),
                            status = document.getString("status") ?: "",
                            idsVoteRemove = document.get("idsVoteRemove") as? List<String>,
                            idsVoteAprove = document.get("idsVoteAprove") as? List<String>
                        )

                        onResult(recyclingPoint)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null) // erro ao buscar
                }
        }


        //para confirmar o ecoponto
        fun confirmRecyclingPoint(recyclingPointId: String,userId: String) {

        }

        //votos para eliminar
        fun deleteRecyclingPoint(recyclingPointId: String,userId: String){

        }

    }



}
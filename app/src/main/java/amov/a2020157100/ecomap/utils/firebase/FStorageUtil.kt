package amov.a2020157100.ecomap.utils.firebase

import amov.a2020157100.ecomap.model.Condition
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.model.RecyclingPoint
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.CompletableFuture
import android.net.Uri


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
                Log.e(TAG, "Error creating recycling point : ", e)
                false
            }
        }


        suspend fun getRecyclingPoints(): List<RecyclingPoint>{
            val db = Firebase.firestore

            val result = db.collection("RecyclingPoints")
                .get()
                .await()

            val recyclingPoints = mutableListOf<RecyclingPoint>()

            for (document in result) {

                val conditionMap = document.get("condition") as? Map<String, Any>

                val conditionObj = if (conditionMap != null) {
                    Condition(
                        creator = conditionMap["creator"] as? String ?: "",
                        state = conditionMap["state"] as? String ?: "",
                        notes = conditionMap["notes"] as? String,
                        imgUrl = conditionMap["imgUrl"] as? String
                    )
                } else {
                    null
                }

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
                        idsVoteAprove = document.get("idsVoteAprove") as? List<String>,
                        condition = conditionObj
                    )
                )
            }

            return recyclingPoints
        }



     fun getRecyclingPoint(recyclingPointId: String, onResult: (RecyclingPoint?) -> Unit) {
            val db = Firebase.firestore

            db.collection("RecyclingPoints")
                .document(recyclingPointId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val conditionMap = document.get("condition") as? Map<String, Any>
                        val conditionObj = if (conditionMap != null) {
                            Condition(
                                creator = conditionMap["creator"] as? String ?: "",
                                state = conditionMap["state"] as? String ?: "",
                                notes = conditionMap["notes"] as? String,
                                imgUrl = conditionMap["imgUrl"] as? String
                            )
                        } else null

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
                            idsVoteAprove = document.get("idsVoteAprove") as? List<String>,
                            condition = conditionObj
                        )

                        onResult(recyclingPoint)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        }



        suspend fun confirmRecyclingPoint(recyclingPointId: String, userId: String) {
            val db = Firebase.firestore
            val recyclingPointRef = db.collection("RecyclingPoints").document(recyclingPointId)

            try {

                recyclingPointRef.update("idsVoteAprove", FieldValue.arrayUnion(userId)).await()
                val snapshot = recyclingPointRef.get().await()

                val currentVotes = snapshot.get("idsVoteAprove") as? List<String> ?: emptyList()
                val currentStatus = snapshot.getString("status")
                if (currentVotes.size >= 2 && currentStatus == Status.PENDING.name) {
                    recyclingPointRef.update("status", Status.FINAL.name).await()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error confirming recycling point: ", e)
            }
        }


        suspend fun deleteRecyclingPoint(recyclingPointId: String, userId: String) {
            val db = Firebase.firestore
            val recyclingPointRef = db.collection("RecyclingPoints").document(recyclingPointId)

            try {

                recyclingPointRef.update("idsVoteRemove", FieldValue.arrayUnion(userId)).await()
                val snapshot = recyclingPointRef.get().await()
                val currentVotes = snapshot.get("idsVoteRemove") as? List<String> ?: emptyList()
                val currentStatus = snapshot.getString("status")
                if (currentStatus != Status.DELETE.name) {
                    recyclingPointRef.update("status", Status.DELETE.name).await()
                }
                if (currentVotes.size >= 3) {
                    recyclingPointRef.delete().await()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting recycling point logic: ", e)
            }
        }

        fun updateCondition(recyclingPointId: String, condition: Condition) {
            val db = Firebase.firestore
            db.collection("RecyclingPoints").document(recyclingPointId)
                .update("condition", condition)
        }

        fun uploadFile(imgPath: String): CompletableFuture<String> {
            val storage = Firebase.storage
            val storageReference = storage.reference
            val file = File(imgPath)
            val newFileNameInsideStorage = storageReference.child(file.name)

            val future = CompletableFuture<String>()
            val uploadTask = newFileNameInsideStorage.putFile(Uri.fromFile(file))

            uploadTask
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { future.completeExceptionally(it) }
                    }
                    newFileNameInsideStorage.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        future.complete(downloadUri.toString())
                    }
                }

            return future
        }


    }



}
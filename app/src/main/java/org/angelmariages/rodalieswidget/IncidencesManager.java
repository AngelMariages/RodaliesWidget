/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.angelmariages.rodalieswidget;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.angelmariages.rodalieswidget.utils.U;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class IncidencesManager {

	private IncidencesGetListener incidencesGetListener;

	public IncidencesManager(IncidencesGetListener incidencesGetListener) {
		this.incidencesGetListener = incidencesGetListener;
	}

	private void getIncidencesFromKey(final String key) {
		final ArrayList<Incidence> incidences = new ArrayList<>();

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference docRef = db.document("incidences/" + key);
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()) {
					DocumentSnapshot document = task.getResult();
					if (document != null && document.exists()) {

						JSONObject jsonObject = new JSONObject(document.getData());
						Iterator<String> keys = jsonObject.keys();
						while (keys.hasNext()) {
							try {
								incidences.add(new Gson().fromJson(jsonObject.getJSONObject(keys.next()).toString(), Incidence.class));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						if (incidencesGetListener != null) {
							incidencesGetListener.onIncidencesGet(key, incidences);
						}
					} else {
						U.log("No such document");
					}
				} else {
					U.log("get failed with " + task.getException());
				}
			}
		});
	}

	public void getIncidenceKeys() {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("incidences")
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
								getIncidencesFromKey(queryDocumentSnapshot.getId());
							}
						} else {
							U.log("Error getting documents." + task.getException());
						}
					}
				});
	}
}


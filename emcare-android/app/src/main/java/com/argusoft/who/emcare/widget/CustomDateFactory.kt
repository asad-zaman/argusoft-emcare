/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.argusoft.who.emcare.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import com.argusoft.who.emcare.R
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.fhir.datacapture.views.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object CustomDateFactory :
    QuestionnaireItemViewHolderFactory(R.layout.custom_date_layout) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var prefixTextView: TextView
            private lateinit var textDateQuestion: TextView
            private lateinit var textInputLayout: TextInputLayout
            private lateinit var textInputEditText: TextInputEditText
            override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

            override fun init(itemView: View) {
                prefixTextView = itemView.findViewById(R.id.prefix)
                textDateQuestion = itemView.findViewById(R.id.question)
                textInputLayout = itemView.findViewById(R.id.textInputLayout)
                textInputEditText = itemView.findViewById(R.id.textInputEditText)
                // Disable direct text input to only allow input from the date picker dialog
                textInputEditText.keyListener = null
                textInputEditText.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
                    // Do not show the date picker dialog when losing focus.
                    if (!hasFocus) return@setOnFocusChangeListener

                    // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
                    // and again in TextInputEditText during layout inflation. As a result, it is
                    // necessary to access the base context twice to retrieve the application object
                    // from the view's context.
                    val context = itemView.context.tryUnwrapContext()!!
                    context.supportFragmentManager.setFragmentResultListener(
                        DatePickerFragment.RESULT_REQUEST_KEY,
                        context,
                        object : FragmentResultListener {
                            // java.time APIs can be used with desugaring
                            @SuppressLint("NewApi")
                            override fun onFragmentResult(requestKey: String, result: Bundle) {
                                val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
                                val month = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
                                val dayOfMonth = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
                                textInputEditText.setText(
                                    LocalDate.of(
                                        year,
                                        // Month values are 1-12 in java.time but 0-11 in
                                        // DatePickerDialog.
                                        month + 1,
                                        dayOfMonth
                                    )
                                        .format(LOCAL_DATE_FORMATTER)
                                )

                                val date = DateType(year, month, dayOfMonth)
                                questionnaireItemViewItem.singleAnswerOrNull =
                                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                                        value = date
                                    }
                                // Clear focus so that the user can refocus to open the dialog
                                textInputEditText.clearFocus()
                                onAnswerChanged(textInputEditText.context)
                            }
                        }
                    )

                    val selectedDate = questionnaireItemViewItem.singleAnswerOrNull?.valueDateType?.localDate
                    DatePickerFragment()
                        .apply { arguments = bundleOf(DatePickerFragment.REQUEST_BUNDLE_KEY_DATE to selectedDate) }
                        .show(context.supportFragmentManager, DatePickerFragment.TAG)
                    // Clear focus so that the user can refocus to open the dialog
                    textDateQuestion.clearFocus()
                }
            }

            @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
                    prefixTextView.visibility = View.VISIBLE
                    prefixTextView.text = questionnaireItemViewItem.questionnaireItem.prefixElement?.getLocalizedText()
                } else {
                    prefixTextView.visibility = View.GONE
                }
                textDateQuestion.text = questionnaireItemViewItem.questionnaireItem.prefixElement?.getLocalizedText()
                textInputEditText.setText(
                    questionnaireItemViewItem.singleAnswerOrNull?.valueDateType?.localDate?.format(
                        LOCAL_DATE_FORMATTER
                    ) ?: ""
                )
                textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text
            }

            override fun displayValidationResult(validationResult: ValidationResult) {
                textInputLayout.error =
                    if (validationResult.getSingleStringValidationMessage() == "") null
                    else validationResult.getSingleStringValidationMessage()
            }

            override fun setReadOnly(isReadOnly: Boolean) {
                textInputEditText.isEnabled = !isReadOnly
                textInputLayout.isEnabled = !isReadOnly
            }
        }

    @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
    val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE!!
}

internal fun Context.tryUnwrapContext(): AppCompatActivity? {
    var context = this
    while (true) {
        when (context) {
            is AppCompatActivity -> return context
            is ContextThemeWrapper -> context = context.baseContext
            else -> return null
        }
    }
}

internal val DateType.localDate
    @RequiresApi(Build.VERSION_CODES.O)
    get() = LocalDate.of(
            year,
            month + 1,
            day,
        )

private fun StringType.getLocalizedText(
    lang: String = Locale.getDefault().toLanguageTag()
): String? {
    return getTranslation(lang) ?: getTranslation(lang.split("-").first()) ?: value
}

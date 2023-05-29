package com.argusoft.who.emcare.widget

import android.text.Spanned
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.argusoft.who.emcare.R
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory

object CustomDisplayViewHolderFactory : QuestionnaireItemViewHolderFactory(R.layout.custom_display_view){
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var question: AppCompatTextView
            private lateinit var hint: AppCompatTextView
            override lateinit var questionnaireViewItem: QuestionnaireViewItem

            override fun init(itemView: View) {
                question = itemView.findViewById(R.id.question)
                hint = itemView.findViewById(R.id.hint)
            }

            override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
                question.updateTextAndVisibility(questionnaireViewItem.questionnaireItem.localizedTextSpanned)
//                hint.updateTextAndVisibility(
//                    questionnaireViewItem.enabledDisplayItems.localizedInstructionsSpanned
//                )
            }

            override fun setReadOnly(isReadOnly: Boolean) {
                // Display type questions have no user input
            }
        }

    fun TextView.updateTextAndVisibility(localizedText: Spanned? = null) {
        text = localizedText
        visibility =
            if (localizedText.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }
    const val WIDGET_EXTENSION = "http://hl7.org/fhir/StructureDefinition/rendering-style"
    const val WIDGET_TYPE = "display"
}
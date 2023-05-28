package com.argusoft.who.emcare.widget

import android.graphics.drawable.GradientDrawable
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.argusoft.who.emcare.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse


object CustomBooleanChoiceViewHolderFactory : QuestionnaireItemViewHolderFactory(R.layout.custom_boolean_choice_view){
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var header: LinearLayout
            private lateinit var question: AppCompatTextView
            private lateinit var hint: AppCompatTextView
            private lateinit var errorTextView: AppCompatTextView
            private lateinit var radioGroup: ConstraintLayout
            private lateinit var yesRadioButton: RadioButton
            private lateinit var noRadioButton: RadioButton
            private lateinit var flow: Flow

            override lateinit var questionnaireViewItem: QuestionnaireViewItem

            override fun init(itemView: View) {
                header = itemView.findViewById(R.id.header)
                question = itemView.findViewById(R.id.question)
                hint = itemView.findViewById(R.id.hint)
                errorTextView = itemView.findViewById(R.id.error_text_at_header)
                radioGroup = itemView.findViewById(R.id.radio_constraint_layout)
                yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
                noRadioButton = itemView.findViewById(R.id.no_radio_button)
                flow = itemView.findViewById(R.id.flow)
            }

            override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
                this.questionnaireViewItem = questionnaireViewItem
                question.updateTextAndVisibility(questionnaireViewItem.questionnaireItem.localizedTextSpanned)
//                hint.updateTextAndVisibility(
//                    questionnaireViewItem.enabledDisplayItems.localizedInstructionsSpanned
//                )
                val styleExtension =
                    questionnaireViewItem.questionnaireItem
                        .getExtensionByUrl(
                            WIDGET_EXTENSION
                        )

                val background = header.background as GradientDrawable
                if(styleExtension != null && styleExtension.value.toString() == "background-color: green;"){
                    background.color = ContextCompat.getColorStateList(header.context,R.color.color_green)
                } else if(styleExtension != null && styleExtension.value.toString() == "background-color: yellow;"){
                    background.color = ContextCompat.getColorStateList(header.context,R.color.color_yellow)
                }else if(styleExtension != null && styleExtension.value.toString() == "background-color: pink;"){
                    background.color = ContextCompat.getColorStateList(header.context, R.color.color_pink )
                }/*else{
                    header.backgroundTintList = ContextCompat.getColorStateList(header.context, R.color.color_white )

                }*/

                yesRadioButton.setLayoutParamsByOrientation()
                noRadioButton.setLayoutParamsByOrientation()

                when (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.value) {
                    true -> {
                        yesRadioButton.isChecked = true
                        noRadioButton.isChecked = false
                    }
                    false -> {
                        noRadioButton.isChecked = true
                        yesRadioButton.isChecked = false
                    }
                    null -> {
                        yesRadioButton.isChecked = false
                        noRadioButton.isChecked = false
                    }
                }

                yesRadioButton.setOnClickListener {
                    if (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() == true
                    ) {
                        questionnaireViewItem.clearAnswer()
                        yesRadioButton.isChecked = false
                    } else {
                        questionnaireViewItem.setAnswer(
                            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                                value = BooleanType(true)
                            }
                        )
                    }
                }

                noRadioButton.setOnClickListener {
                    if (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() ==
                        false
                    ) {
                        questionnaireViewItem.clearAnswer()
                        noRadioButton.isChecked = false
                    } else {
                        questionnaireViewItem.setAnswer(
                            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                                value = BooleanType(false)
                            }
                        )
                    }
                }

//                questionnaireViewItem.questionnaireItem.answerOption
//                    .map { answerOption -> View.generateViewId() to answerOption }
//                    .onEach { populateViewWithAnswerOption(it.first, it.second) }
//                    .map { it.first }
//                    .let { flow.referencedIds = it.toIntArray() }
                displayValidationResult(questionnaireViewItem.validationResult)
            }

            private fun populateViewWithAnswerOption(
                viewId: Int,
                answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent
            ) {
                val radioButtonItem =
                    LayoutInflater.from(radioGroup.context).inflate(com.google.android.fhir.datacapture.R.layout.radio_button, null)
                var isCurrentlySelected = questionnaireViewItem.isAnswerOptionSelected(answerOption)
                val radioButton =
                    radioButtonItem.findViewById<RadioButton>(com.google.android.fhir.datacapture.R.id.radio_button).apply {
                        id = viewId
                        text = answerOption.value.displayString(header.context)
                        setCompoundDrawablesRelative(
                            answerOption.itemAnswerOptionImage(radioGroup.context),
                            null,
                            null,
                            null
                        )
                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        isChecked = isCurrentlySelected
                        setOnClickListener { radioButton ->
                            isCurrentlySelected = !isCurrentlySelected
                            when (isCurrentlySelected) {
                                true -> {
                                    updateAnswer(answerOption)
                                    val buttons = radioGroup.children.asIterable().filterIsInstance<RadioButton>()
                                    buttons.forEach { button -> uncheckIfNotButtonId(radioButton.id, button) }
                                }
                                false -> {
                                    questionnaireViewItem.clearAnswer()
                                    (radioButton as RadioButton).isChecked = false
                                }
                            }
                        }
                    }
                radioGroup.addView(radioButton)
                flow.addView(radioButton)
            }

            private fun uncheckIfNotButtonId(checkedId: Int, button: RadioButton) {
                if (button.id != checkedId) button.isChecked = false
            }

            private fun updateAnswer(answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent) {
                questionnaireViewItem.setAnswer(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                        value = answerOption.value
                    }
                )
            }

            override fun setReadOnly(isReadOnly: Boolean) {
                for (i in 0 until radioGroup.childCount) {
                    val view = radioGroup.getChildAt(i)
                    view.isEnabled = !isReadOnly
                }
            }

            private fun RadioButton.setLayoutParamsByOrientation(
            ) {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            }

            private fun displayValidationResult(validationResult: ValidationResult) {
                when (validationResult) {
                    is NotValidated,
                    Valid -> errorTextView.showErrorText(isErrorTextVisible = false)
                    is Invalid -> {
                        errorTextView.showErrorText(errorText = validationResult.getSingleStringValidationMessage())
                    }
                }
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

    fun TextView.showErrorText(errorText: String? = null, isErrorTextVisible: Boolean = true) {
        visibility =
            when (isErrorTextVisible) {
                true -> {
                    LinearLayout.VISIBLE
                }
                false -> {
                    LinearLayout.GONE
                }
            }
        text = errorText
    }

    const val WIDGET_EXTENSION = "http://hl7.org/fhir/StructureDefinition/rendering-style"
    const val WIDGET_TYPE = "background-color:"
}
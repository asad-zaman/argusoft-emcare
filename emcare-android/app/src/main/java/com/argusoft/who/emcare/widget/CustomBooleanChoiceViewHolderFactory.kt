package com.argusoft.who.emcare.widget

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.argusoft.who.emcare.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse


object CustomBooleanChoiceViewHolderFactory :
    QuestionnaireItemViewHolderFactory(R.layout.custom_boolean_choice_view) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var header: HeaderView
            private lateinit var radioGroup: ConstraintLayout
            private lateinit var yesRadioButton: RadioButton
            private lateinit var noRadioButton: RadioButton
            private lateinit var flow: Flow

            override lateinit var questionnaireViewItem: QuestionnaireViewItem

            override fun init(itemView: View) {
                header = itemView.findViewById(R.id.header)
                radioGroup = itemView.findViewById(R.id.radio_constraint_layout)
                yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
                noRadioButton = itemView.findViewById(R.id.no_radio_button)
                flow = itemView.findViewById(R.id.flow)
            }

            override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
                this.questionnaireViewItem = questionnaireViewItem
                header.bind(questionnaireViewItem)
                header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
                radioGroup.removeViews(1, radioGroup.childCount - 1)
                val choiceOrientation =
                    questionnaireViewItem.questionnaireItem.choiceOrientation
                        ?: ChoiceOrientationTypes.VERTICAL
                with(flow) {
                    when (choiceOrientation) {
                        ChoiceOrientationTypes.HORIZONTAL -> {
                            setOrientation(Flow.HORIZONTAL)
                            setWrapMode(Flow.WRAP_CHAIN)
                        }

                        ChoiceOrientationTypes.VERTICAL -> {
                            setOrientation(Flow.VERTICAL)
                            setWrapMode(Flow.WRAP_NONE)
                        }
                    }
                }

                try {
                    val styleExtension =
                        questionnaireViewItem.questionnaireItem
                            .getExtensionByUrl(
                                WIDGET_EXTENSION
                            )
                    val background = header.background as GradientDrawable
                    if (styleExtension != null && styleExtension.value.toString() == "background-color: green;") {
                        background.color =
                            ContextCompat.getColorStateList(header.context, R.color.color_green)
                    } else if (styleExtension != null && styleExtension.value.toString() == "background-color: yellow;") {
                        background.color =
                            ContextCompat.getColorStateList(header.context, R.color.color_yellow)
                    } else if (styleExtension != null && styleExtension.value.toString() == "background-color: pink;") {
                        background.color =
                            ContextCompat.getColorStateList(header.context, R.color.color_pink)
                    } else if (styleExtension != null && styleExtension.value.toString()
                            .contains("#")
                    ) {
                        background.color = ColorStateList.valueOf(
                            Color.parseColor(
                                "#" +
                                        styleExtension.value.toString().substringAfterLast("#")
                                            .replace(";", "").trim()
                            )
                        )
                    }
                }catch (e: Exception) {
                    print(e.localizedMessage)
                }

                yesRadioButton.setLayoutParamsByOrientation(choiceOrientation)
                noRadioButton.setLayoutParamsByOrientation(choiceOrientation)


                questionnaireViewItem.enabledAnswerOptions
                    .map { answerOption -> View.generateViewId() to answerOption }
                    .onEach { populateViewWithAnswerOption(it.first, it.second, choiceOrientation) }
                    .map { it.first }
                    .let { flow.referencedIds = it.toIntArray() }

                displayValidationResult(questionnaireViewItem.validationResult)
            }

            private fun populateViewWithAnswerOption(
                viewId: Int,
                answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent,
                choiceOrientation: ChoiceOrientationTypes
            ) {
                val radioButtonItem =
                    LayoutInflater.from(radioGroup.context)
                        .inflate(com.google.android.fhir.datacapture.R.layout.radio_button, null)
                var isCurrentlySelected = questionnaireViewItem.isAnswerOptionSelected(answerOption)
                val radioButton =
                    radioButtonItem.findViewById<RadioButton>(com.google.android.fhir.datacapture.R.id.radio_button)
                        .apply {
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
                                    when (choiceOrientation) {
                                        ChoiceOrientationTypes.HORIZONTAL -> ViewGroup.LayoutParams.WRAP_CONTENT
                                        ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
                                    },
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            isChecked = isCurrentlySelected
                            setOnClickListener { radioButton ->
                                isCurrentlySelected = !isCurrentlySelected
                                when (isCurrentlySelected) {
                                    true -> {
                                        updateAnswer(answerOption)
                                        val buttons = radioGroup.children.asIterable()
                                            .filterIsInstance<RadioButton>()
                                        buttons.forEach { button ->
                                            uncheckIfNotButtonId(
                                                radioButton.id,
                                                button
                                            )
                                        }
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
                choiceOrientation: ChoiceOrientationTypes
            ) {
                layoutParams =
                    LinearLayout.LayoutParams(
                        when (choiceOrientation) {
                            ChoiceOrientationTypes.HORIZONTAL -> /* width= */ 0
                            ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
                        },
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        /* weight= */ 1.0f
                    )
            }

            private fun displayValidationResult(validationResult: ValidationResult) {
                when (validationResult) {
                    is NotValidated,
                    Valid -> header.showErrorText(isErrorTextVisible = false)

                    is Invalid -> {
                        header.showErrorText(errorText = validationResult.getSingleStringValidationMessage())
                    }
                }
            }
        }

    const val WIDGET_EXTENSION = "http://hl7.org/fhir/StructureDefinition/rendering-style"
    const val WIDGET_TYPE = "background-color:"
}
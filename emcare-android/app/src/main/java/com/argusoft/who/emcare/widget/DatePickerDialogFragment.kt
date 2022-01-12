package com.argusoft.who.emcare.widget

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.LocalDate

internal class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
  @SuppressLint("NewApi") // Suppress warnings for java.time APIs
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // Use the current date as the default date in the picker
    val today = this.arguments?.get(REQUEST_BUNDLE_KEY_DATE) as? LocalDate ?: LocalDate.now()

    // Create a new instance of DatePickerDialog and return it
    return DatePickerDialog(
      requireContext(),
      this,
      today.year,
      // month values are 1-12 in java.time but DatePickerDialog expects 0-11
      today.monthValue - 1,
      today.dayOfMonth
    )
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    setFragmentResult(
      RESULT_REQUEST_KEY,
      bundleOf(
        RESULT_BUNDLE_KEY_YEAR to year,
        RESULT_BUNDLE_KEY_MONTH to month,
        RESULT_BUNDLE_KEY_DAY_OF_MONTH to dayOfMonth
      )
    )
    dismiss()
  }

  companion object {
    const val TAG = "date-picker-fragment"
    const val RESULT_REQUEST_KEY = "date-picker-request-key"
    const val RESULT_BUNDLE_KEY_YEAR = "date-picker-bundle-key-year"
    const val RESULT_BUNDLE_KEY_MONTH = "date-picker-bundle-key-month"
    const val RESULT_BUNDLE_KEY_DAY_OF_MONTH = "date-picker-bundle-day-of-month"
    const val REQUEST_BUNDLE_KEY_DATE = "date-picker-request-bundle-key-date"
  }
}

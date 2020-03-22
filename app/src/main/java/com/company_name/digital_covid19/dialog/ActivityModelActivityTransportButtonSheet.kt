/*
*  ActivityModelActivityTransportButtonSheet
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.dialog

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.company_name.digital_covid19.R


class ActivityModelActivityTransportButtonSheet: BottomSheetDialogFragment() {

	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
	
		return inflater.inflate(R.layout.activity_model_activity_transport_button_sheet, container, false)
	}
}

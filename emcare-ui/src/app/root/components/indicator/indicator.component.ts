import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FhirService, ToasterService } from 'src/app/shared';
import { forkJoin } from 'rxjs';
import { AuthGuard } from 'src/app/auth/auth.guard';

@Component({
  selector: 'app-indicator',
  templateUrl: './indicator.component.html',
  styleUrls: ['./indicator.component.scss']
})
export class IndicatorComponent implements OnInit {

  isEdit: boolean = false;
  editId: string;
  indicatorForm: FormGroup;
  submitted: boolean;
  isAllowed = true;
  facilityArr = [];
  displayTypeArr = [
    { id: 'count', name: 'Count' },
    { id: 'pie', name: 'Pie' },
    { id: 'bar', name: 'Bar' },
    { id: 'scatter', name: 'Scatter' }
  ];
  codeArr = [];
  selectedNumeratorArr = [];
  selectedDenominatorArr = [];
  conditionArr = [
    { id: '=', name: '= (equal)' },
    { id: '<', name: '< (less than)' },
    { id: '>', name: '> (greater than)' },
    { id: '<=', name: '<= (less than equal to)' },
    { id: '>=', name: '>= (greater than equal to)' }
  ];
  valueTypeArr = [
    { id: 'boolean', name: 'Boolean' },
    { id: 'text', name: 'String' },
    { id: 'date', name: 'Date' },
    { id: 'number', name: 'Number' }
  ];
  apiBusy = true;
  isAddFeature = true;
  isEditFeature = true;
  nEqArr = [];
  dEqArr = [];
  eqConditionArr = [
    { id: '+', name: '+ Plus' },
    { id: '-', name: '- Minus' },
    { id: '*', name: '* Multiplication' },
    { id: '/', name: '/ Division)' }
  ];
  selectedEqs = [];
  finalEqs = [];

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    forkJoin([
      this.fhirService.getFacility(),
      this.fhirService.getAllCodes()
    ]).subscribe(result => {
      if (result && result.length > 0) {
        this.setFailityResponse(result[0]);
        this.setCodesResponse(result[1]);
        this.apiBusy = false;
        //  toDo when both api succeds then only this should be init
        //  check and set values
        const routeParams = this.route.snapshot.paramMap;
        this.editId = routeParams.get('id');
        this.initIndicatorForm();
      }
    }, (_e) => {
      this.toasterService.showToast('error', 'Server issue!', 'EM CARE !!');
    });
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAddFeature = res.featureJSON['canAdd'];
        this.isEditFeature = res.featureJSON['canEdit'];
        if (this.isAddFeature && this.isEditFeature) {
          this.isAllowed = true;
        } else if (this.isAddFeature && !this.isEdit) {
          this.isAllowed = true;
        } else if (!this.isEditFeature && this.isEdit) {
          this.isAllowed = false;
        } else if (!this.isAddFeature && this.isEdit) {
          this.isAllowed = true;
        } else if (this.isEditFeature && this.isEdit) {
          this.isAllowed = true;
        } else {
          this.isAllowed = false;
        }
      }
    });
  }

  checkEditParam() {
    if (this.editId) {
      this.isEdit = true;
      this.fhirService.getIndicatorById(this.editId).subscribe((res: any) => {
        if (res) {
          this.setCurrentIndicator(res);
        }
      });
    }
  }

  getFacilityById(facilityId) {
    return this.facilityArr.find(el => el.id === facilityId);
  }

  getDSTById(dst) {
    return this.displayTypeArr.find(el => el.id === dst);
  }

  setCurrentIndicator(currentIndicator) {
    this.indicatorForm.patchValue({
      codeName: currentIndicator.indicatorCode,
      indicatorName: currentIndicator.indicatorName,
      indicatorDescription: currentIndicator.description,
      facility: this.getFacilityById(currentIndicator.facilityId),
      displayType: this.getDSTById(currentIndicator.displayType),
      numerators: currentIndicator.numeratorEquation.length > 0 ?
        this.setNumerators(currentIndicator.numeratorEquation) : [],
      denominators: currentIndicator.denominatorEquation.length > 0 ?
        this.setDenominators(currentIndicator.denominatorEquation) : [],
      numeratorEquation: currentIndicator.numeratorIndicatorEquation,
      denominatorEquation: currentIndicator.denominatorIndicatorEquation
    });
    this.setNumeratorEquationArr();
  }

  setNumerators(numeratorEquation) {
    const numeratorArr = [];
    numeratorEquation.forEach(element => {
      const obj = {
        numeratorId: element.numeratorId,
        code: element.code ? this.codeArr.find(el => el.code === element.code) : null,
        condition: element.condition ? this.conditionArr.find(el => el.id === element.condition) : null,
        value: element.value,
        valueType: element.valueType ? this.valueTypeArr.find(el => el.id === element.valueType) : null,
        eqIdentifier: element.eqIdentifier,
        appendOtherNumeratorDropdown: true
      }
      numeratorArr.push(obj);
      this.addNumerator();
    });
    this.getNumerators().patchValue(numeratorArr);
    return numeratorArr;
  }

  setDenominators(denominatorEquation) {
    const denominatorArr = [];
    denominatorEquation.forEach(element => {
      const obj = {
        denominatorId: element.denominatorId,
        code: element.code ? this.codeArr.find(el => el.code === element.code) : null,
        condition: element.condition ? this.conditionArr.find(el => el.id === element.condition) : null,
        value: element.value,
        valueType: element.valueType ? this.valueTypeArr.find(el => el.id === element.valueType) : null,
        eqIdentifier: element.eqIdentifier,
        appendOtherDenominatorDropdown: true
      }
      denominatorArr.push(obj);
      this.addDenominator();
    });
    this.getDenominators().patchValue(denominatorArr);
    return denominatorArr;
  }

  setCodesResponse(res) {
    this.codeArr = [];
    if (res) {
      this.codeArr = res;
    }
  }

  initIndicatorForm() {
    this.indicatorForm = this.formBuilder.group({
      codeName: ['', [Validators.required]],
      indicatorName: ['', [Validators.required]],
      indicatorDescription: ['', [Validators.required]],
      facility: ['', [Validators.required]],
      displayType: ['', [Validators.required]],
      numerators: this.editId ?
        this.formBuilder.array([]) : this.formBuilder.array([this.newNumeratorAddition()]),
      denominators: this.editId ?
        this.formBuilder.array([]) : this.formBuilder.array([this.newDenominatorAddition()]),
      numeratorEquation: ['', [Validators.required]],
      denominatorEquation: ['', [Validators.required]]
    });
    this.checkEditParam();
  }

  getNumerators(): FormArray {
    return this.indicatorForm.get("numerators") as FormArray;
  }

  getDenominators(): FormArray {
    return this.indicatorForm.get("denominators") as FormArray;
  }

  newNumeratorAddition(): FormGroup {
    return this.formBuilder.group({
      numeratorId: null,
      code: null,
      condition: null,
      value: null,
      valueType: null,
      eqIdentifier: null,
      appendOtherNumeratorDropdown: false
    })
  }

  newDenominatorAddition(): FormGroup {
    return this.formBuilder.group({
      denominatorId: null,
      code: null,
      condition: null,
      value: null,
      valueType: null,
      eqIdentifier: null,
      appendOtherDenominatorDropdown: false
    })
  }

  addNumerator() {
    this.getNumerators().push(this.newNumeratorAddition());
  }

  addDenominator() {
    this.getDenominators().push(this.newDenominatorAddition());
  }

  removeNumerator(i: number) {
    this.getNumerators().removeAt(i);
    this.nEqArr.splice(i, 1);
    this.selectedEqs.splice(i, 1);
    this.finalEqs.splice(i, 1);
  }

  removeDenominator(i: number) {
    this.getDenominators().removeAt(i);
  }

  setFailityResponse(res) {
    if (res) {
      res.forEach(element => {
        this.facilityArr.push({
          id: element.facilityId,
          name: element.facilityName,
          organizationName: element.organizationName
        });
      });
    }
  }

  get f() {
    return this.indicatorForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.indicatorForm.valid) {
      const body = this.getRequestBody(this.indicatorForm.value);
      if (this.isEdit) {
        body['indicatorId'] = this.editId;
      }
      this.fhirService.addIndicator(body).subscribe(() => {
        if (this.isEdit) {
          this.toasterService.showToast('success', 'Indicator updated successfully!', 'EMCARE !!');
        } else {
          this.toasterService.showToast('success', 'Indicator added successfully!', 'EMCARE !!');
        }
        this.router.navigate(['/indicator-list']);
      }, () => {
        this.toasterService.showToast('error', 'Server issue!', 'EMCARE !!');
      });
    }
  }

  getRequestBody(formValue) {
    return {
      "indicatorCode": formValue.codeName,
      "indicatorName": formValue.indicatorName,
      "description": formValue.indicatorDescription,
      "facilityId": formValue.facility.id,
      "numeratorIndicatorEquation": formValue.numeratorEquation,
      "denominatorIndicatorEquation": formValue.denominatorEquation,
      "displayType": formValue.displayType.id,
      "numeratorEquations": this.getNumeratorsBody(),
      "denominatorEquations": this.getDenominatorsBody()
    }
  }

  getNumeratorsBody() {
    const tempArr = [];
    this.getNumerators().controls.forEach(element => {
      tempArr.push({
        numeratorId: element.value.numeratorId,
        codeId: element.value.code.codeId,
        code: element.value.code.code,
        condition: element.value.condition.id,
        value: element.value.value,
        valueType: element.value.valueType.id,
        eqIdentifier: element.value.eqIdentifier
      });
    });
    return tempArr;
  }

  getDenominatorsBody() {
    const tempArr = [];
    this.getDenominators().controls.forEach(element => {
      tempArr.push({
        denominatorId: element.value.denominatorId,
        codeId: element.value.code.codeId,
        code: element.value.code.code,
        condition: element.value.condition.id,
        value: element.value.value,
        valueType: element.value.valueType.id,
        eqIdentifier: element.value.eqIdentifier
      });
    });
    return tempArr;
  }

  getSelectedNumeratorsId() {
    this.selectedNumeratorArr = [];
    this.getNumerators().controls.forEach(element => {
      if (element && element.value.code) {
        this.selectedNumeratorArr.push(element.value.code.codeId);
      }
    });
  }

  getSelectedDenominatorsId() {
    this.selectedDenominatorArr = [];
    this.getDenominators().controls.forEach(element => {
      if (element && element.value.code) {
        this.selectedDenominatorArr.push(element.value.code.codeId);
      }
    });
  }

  onCodeSelected(event, index, isNumerator) {
    if (event) {
      if (isNumerator) {
        const isCodeAlreadySelected = this.selectedNumeratorArr.indexOf(event.value.codeId) > -1 ? true : false;
        this.getSelectedNumeratorsId();
        this.getNumerators().controls[index].patchValue({ appendOtherNumeratorDropdown: true });
        if (isCodeAlreadySelected) {
          this.getNumerators().controls[index].patchValue({
            code: null,
            appendOtherNumeratorDropdown: false
          });
          this.toasterService.showToast('error', 'Same code can not be selected again !!', 'EMCARE !!');
        }
      } else {
        const isCodeAlreadySelected = this.selectedDenominatorArr.indexOf(event.value.codeId) > -1 ? true : false;
        this.getSelectedDenominatorsId();
        this.getDenominators().controls[index].patchValue({ appendOtherDenominatorDropdown: true });
        if (isCodeAlreadySelected) {
          this.getDenominators().controls[index].patchValue({
            code: null,
            appendOtherDenominatorDropdown: false
          });
          this.toasterService.showToast('error', 'Same code can not be selected again !!', 'EMCARE !!');
        }
      }
    }
  }

  setNumeratorEquationArr() {
    this.nEqArr = [];
    this.getNumerators().controls.forEach(element => {
      if (element.value.eqIdentifier) {
        this.nEqArr.push({ id: element.value.eqIdentifier, name: element.value.eqIdentifier });
      }
    });
    return this.nEqArr;
  }

  setDenominatorEquationArr() {
    this.dEqArr = [];
    this.getDenominators().controls.forEach(element => {
      if (element.value.eqIdentifier) {
        this.dEqArr.push({ id: element.value.eqIdentifier, name: element.value.eqIdentifier });
      }
    });
    return this.dEqArr;
  }

  onEquationSelected(event, index, isNumerator) {
    if (event) {
      if (isNumerator) {
        if (this.selectedEqs.length == 0) {
          this.selectedEqs.push(event.value.id);
        } else {
          if (this.selectedEqs.indexOf(event.value.id) >= 0) {
            this.toasterService.showToast('error', 'Please Select other Equation!', 'EM CARE !!')
            this.finalEqs.splice(index, 1);
            this.finalEqs[index] = null;
            this.selectedEqs.splice(index, 1);
          } else {
            this.selectedEqs.push(event.value.id);
          }
        }
        console.log(this.finalEqs, this.selectedEqs);
      } else {

      }
    }
  }
}

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
    { id: null, name: 'Any' },
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
  selectedNumEqs = [];
  selectedDenEqs = [];
  finalNumEqs = [];
  finalDenEqs = [];
  numEqCOnditionArr = [];
  denEqCOnditionArr = [];
  numeratorEquationStringArr = [];
  denominatorEquationStringArr = [];

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
        if (this.isEdit) {
          this.isAllowed = this.isEditFeature || !this.isAddFeature ? true : false;
        } else if (this.isAddFeature) {
          this.isAllowed = this.isEditFeature || !this.isEdit ? true : false;
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
    this.numeratorEquationStringArr = JSON.parse(currentIndicator.numeratorEquationString);
    this.denominatorEquationStringArr = JSON.parse(currentIndicator.denominatorEquationString);
    let numeratorEquation = '';
    if (this.numeratorEquationStringArr && this.numeratorEquationStringArr.length > 0) {
      this.numeratorEquationStringArr.forEach(element => {
        numeratorEquation += element
      });
    }
    let denominatorEquation = '';
    if (this.denominatorEquationStringArr && this.denominatorEquationStringArr.length > 0) {
      this.denominatorEquationStringArr.forEach(element => {
        denominatorEquation += element
      });
    }
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
      numeratorEquation: numeratorEquation,
      denominatorEquation: denominatorEquation
    });
    this.setNumeratorEquationArr();
    this.setDenominatorEquationArr();
    this.numeratorEquationStringArr && this.numeratorEquationStringArr.forEach((element, index) => {
      if (index % 2 == 0) {
        this.finalNumEqs.push({ id: element, name: element });
        this.selectedNumEqs.push(element);
      } else {
        this.numEqCOnditionArr.push(this.eqConditionArr.find(el => el.id === element));
      }
    });
    this.denominatorEquationStringArr && this.denominatorEquationStringArr.forEach((element, index) => {
      if (index % 2 == 0) {
        this.finalDenEqs.push({ id: element, name: element });
        this.selectedDenEqs.push(element);
      } else {
        this.denEqCOnditionArr.push(this.eqConditionArr.find(el => el.id === element));
      }
    });
  }

  setNumerators(numeratorEquation) {
    const numeratorArr = [];
    numeratorEquation.forEach(element => {
      const obj = {
        numeratorId: element.numeratorId,
        code: element.code ? this.codeArr.find(el => el.code === element.code) : null,
        condition: element.condition ? this.conditionArr.find(el => el.id === element.condition) : null,
        value: element.value,
        valueType: element.valueType || element.valueType === null ? this.valueTypeArr.find(el => el.id === element.valueType) : null,
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
        valueType: element.valueType || element.valueType === null ? this.valueTypeArr.find(el => el.id === element.valueType) : null,
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
      numeratorEquation: [''],
      denominatorEquation: ['']
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
      appendOtherNumeratorDropdown: false,
      isShow: true,
      isValueDropdown: false,
      valueDropdownArr: []
    });
  }

  newDenominatorAddition(): FormGroup {
    return this.formBuilder.group({
      denominatorId: null,
      code: null,
      condition: null,
      value: null,
      valueType: null,
      eqIdentifier: null,
      appendOtherDenominatorDropdown: false,
      isShow: true,
      isValueDropdown: false,
      valueDropdownArr: []
    });
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
    this.finalNumEqs.splice(i, 1);
    this.setSelectedNumEqsArr();
  }

  removeDenominator(i: number) {
    this.getDenominators().removeAt(i);
    this.dEqArr.splice(i, 1);
    this.finalDenEqs.splice(i, 1);
    this.setSelectedDenEqsArr();
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

  get getFormConfrols() {
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
          this.toasterService.showToast('success', 'Indicator updated successfully!', 'EMCARE!');
        } else {
          this.toasterService.showToast('success', 'Indicator added successfully!', 'EMCARE!');
        }
        this.router.navigate(['/indicator-list']);
      }, () => {
        this.toasterService.showToast('error', 'Server issue!', 'EMCARE!');
      });
    }
  }

  getNumEquation() {
    let numEq = '';
    this.numeratorEquationStringArr = [];
    this.finalNumEqs.forEach((element, index) => {
      if (element) {
        numEq += element.id;
        this.numeratorEquationStringArr.push(element.id);
        if (index < this.finalNumEqs.length - 1) {
          numEq += this.numEqCOnditionArr[index].id;
          this.numeratorEquationStringArr.push(this.numEqCOnditionArr[index].id);
        }
      }
    });
    return numEq;
  }

  getDenEquation() {
    let denEq = '';
    this.denominatorEquationStringArr = [];
    this.finalDenEqs.forEach((element, index) => {
      if (element) {
        denEq += element.id;
        this.denominatorEquationStringArr.push(element.id);
        if (index < this.finalDenEqs.length - 1) {
          denEq += this.denEqCOnditionArr[index].id;
          this.denominatorEquationStringArr.push(this.denEqCOnditionArr[index].id);
        }
      }
    });
    return denEq;
  }

  getRequestBody(formValue) {
    return {
      "indicatorCode": formValue.codeName,
      "indicatorName": formValue.indicatorName,
      "description": formValue.indicatorDescription,
      "facilityId": formValue.facility.id,
      "numeratorIndicatorEquation": this.getNumEquation(),
      "denominatorIndicatorEquation": this.getDenEquation(),
      "displayType": formValue.displayType.id,
      "numeratorEquations": this.getNumeratorsBody(),
      "denominatorEquations": this.getDenominatorsBody(),
      "numeratorEquationString": JSON.stringify(this.numeratorEquationStringArr),
      "denominatorEquationString": JSON.stringify(this.denominatorEquationStringArr),
    }
  }

  getNumeratorsBody() {
    const tempArr = [];
    this.getNumerators().controls.forEach(element => {
      tempArr.push({
        numeratorId: element.value.numeratorId,
        codeId: element.value.code ? element.value.code.codeId : null,
        code: element.value.code ? element.value.code.code : null,
        condition: element.value.condition ? element.value.condition.id : null,
        value: typeof element.value.value === 'object' && element.value.value !== null ?
          element.value.value.id : element.value.value,
        valueType: element.value.valueType ? element.value.valueType.id : null,
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
        codeId: element.value.code ? element.value.code.codeId : null,
        code: element.value.code ? element.value.code.code : null,
        condition: element.value.condition ? element.value.condition.id : null,
        value: typeof element.value.value === 'object' && element.value.value !== null ?
          element.value.value.id : element.value.value,
        valueType: element.value.valueType ? element.value.valueType.id : null,
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
        } else {
          this.getNumerators().controls[index].patchValue({
            condition: event.value.condition ?
              this.conditionArr.find(el => el.id === event.value.condition[0].charAt(0)) : null,
            valueType: event.value.valueType ?
              this.valueTypeArr.find(el => el.name === event.value.valueType) : null,
          });
          if (event.value.value && event.value.value.length > 0) {
            let dArr = [];
            event.value.value.forEach(el => {
              dArr.push({ id: el, name: el });
            });
            this.getNumerators().controls[index].patchValue({
              isValueDropdown: true, valueDropdownArr: dArr
            });
          } else {
            this.getNumerators().controls[index].patchValue({
              isValueDropdown: false, valueDropdownArr: []
            });
          }
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
        } else {
          this.getDenominators().controls[index].patchValue({
            condition: event.value.condition ?
              this.conditionArr.find(el => el.id === event.value.condition[0].charAt(0)) : null,
            valueType: event.value.valueType ?
              this.valueTypeArr.find(el => el.name === event.value.valueType) : null,
          });
          if (event.value.value && event.value.value.length > 0) {
            let dArr = [];
            event.value.value.forEach(el => {
              dArr.push({ id: el, name: el });
            });
            this.getDenominators().controls[index].patchValue({
              isValueDropdown: true, valueDropdownArr: dArr
            });
          } else {
            this.getDenominators().controls[index].patchValue({
              isValueDropdown: false, valueDropdownArr: []
            });
          }
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
        if (this.selectedNumEqs.length == 0) {
          this.selectedNumEqs.push(event.value.id);
        } else {
          if (this.finalNumEqs.length < this.selectedNumEqs.length) {
            this.setSelectedNumEqsArr();
          } else if (this.selectedNumEqs.indexOf(event.value.id) >= 0) {
            this.toasterService.showToast('error', 'Please Select other Equation!', 'EM CARE!')
            this.finalNumEqs[index] = null;
            this.setSelectedNumEqsArr();
          } else {
            this.setSelectedNumEqsArr();
          }
        }
      } else {
        if (this.selectedDenEqs.length == 0) {
          this.selectedDenEqs.push(event.value.id);
        } else {
          if (this.finalDenEqs.length < this.selectedDenEqs.length) {
            this.setSelectedDenEqsArr();
          } else if (this.selectedDenEqs.indexOf(event.value.id) >= 0) {
            this.toasterService.showToast('error', 'Please Select other Equation!', 'EM CARE !!')
            this.finalDenEqs[index] = null;
            this.setSelectedDenEqsArr();
          } else {
            this.setSelectedDenEqsArr();
          }
        }
      }
    }
  }

  setSelectedNumEqsArr() {
    this.selectedNumEqs = [];
    this.finalNumEqs.forEach(element => {
      if (element)
        this.selectedNumEqs.push(element.id);
    });
  }

  setSelectedDenEqsArr() {
    this.selectedDenEqs = [];
    this.finalDenEqs.forEach(element => {
      if (element)
        this.selectedDenEqs.push(element.id);
    });
  }

  showHideCurrentEquation(i) {
    const currValue = this.getNumerators().controls[i].value.isShow;
    this.getNumerators().controls[i].patchValue({
      isShow: !currValue
    });
  }

  showHideCurrentDenominatorEquation(i) {
    const currValue = this.getDenominators().controls[i].value.isShow;
    this.getDenominators().controls[i].patchValue({
      isShow: !currValue
    });
  }
}

import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FhirService, ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-indicator',
  templateUrl: './indicator.component.html',
  styleUrls: ['./indicator.component.scss']
})
export class IndicatorComponent implements OnInit {

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

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getCodeList();
    this.getFacilities();
    this.initIndicatorForm();
  }

  getCodeList() {
    this.codeArr = [];
    this.fhirService.getAllCodes().subscribe((res: any) => {
      if (res) {
        this.codeArr = res;
      }
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  initIndicatorForm() {
    this.indicatorForm = this.formBuilder.group({
      codeName: ['', [Validators.required]],
      indicatorName: ['', [Validators.required]],
      indicatorDescription: ['', [Validators.required]],
      facility: ['', [Validators.required]],
      displayType: ['', [Validators.required]],
      numerators: this.formBuilder.array([this.newNumeratorAddition()]),
      denominators: this.formBuilder.array([this.newDenominatorAddition()]),
      numeratorEquation: ['', [Validators.required]],
      denominatorEquation: ['', [Validators.required]]
    });
  }

  getNumerators(): FormArray {
    return this.indicatorForm.get("numerators") as FormArray;
  }

  getDenominators(): FormArray {
    return this.indicatorForm.get("denominators") as FormArray;
  }

  newNumeratorAddition(): FormGroup {
    return this.formBuilder.group({
      code: null,
      condition: null,
      value: null,
      valueType: null,
      eqIdentifier: null
    })
  }

  newDenominatorAddition(): FormGroup {
    return this.formBuilder.group({
      code: null,
      condition: null,
      value: null,
      valueType: null,
      eqIdentifier: null
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
  }

  removeDenominator(i: number) {
    this.getDenominators().removeAt(i);
  }

  getFacilities() {
    this.fhirService.getFacility().subscribe((res: Array<any>) => {
      if (res) {
        res.forEach(element => {
          this.facilityArr.push({
            id: element.facilityId,
            name: element.facilityName,
            organizationName: element.organizationName
          });
        });
      }
    });
  }

  get f() {
    return this.indicatorForm.controls;
  }

  saveData() {
    this, this.submitted = true;
    if (this.indicatorForm.valid) {
      const body = this.getRequestBody(this.indicatorForm.value);
      this.fhirService.addIndicator(body).subscribe(() => {
        this.toasterService.showToast('success', 'Indicator added successfully!', 'EMCARE !!');
        this.router.navigate(['/dashboard']);
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
        "codeId": element.value.code.codeId,
        "code": element.value.code.code,
        "condition": element.value.condition.id,
        "value": element.value.value,
        "valueType": element.value.valueType.id,
        "eqIdentifier": element.value.eqIdentifier
      });
    });
    return tempArr;
  }

  getDenominatorsBody() {
    const tempArr = [];
    this.getDenominators().controls.forEach(element => {
      tempArr.push({
        "codeId": element.value.code.codeId,
        "code": element.value.code.code,
        "condition": element.value.condition.id,
        "value": element.value.value,
        "valueType": element.value.valueType.id,
        "eqIdentifier": element.value.eqIdentifier
      });
    });
    return tempArr;
  }

  getSelectedNumeratorsId() {
    this.selectedNumeratorArr = [];
    this.getNumerators().controls.forEach(element => {
      this.selectedNumeratorArr.push(element.value.code.codeId);
    });
  }

  getSelectedDenominatorsId() {
    this.selectedDenominatorArr = [];
    this.getDenominators().controls.forEach(element => {
      this.selectedDenominatorArr.push(element.value.code.codeId);
    });
  }

  onCodeSelected(event, index, isNumerator) {
    if (event) {
      if (isNumerator) {
        const isCodeAlreadySelected = this.selectedNumeratorArr.indexOf(event.value.codeId) > -1 ? true : false;
        this.getSelectedNumeratorsId();
        if (isCodeAlreadySelected) {
          this.getNumerators().controls[index].patchValue({
            code: null
          });
          this.toasterService.showToast('error', 'Same code can not be selected again !!', 'EMCARE !!');
        }
      } else {
        const isCodeAlreadySelected = this.selectedDenominatorArr.indexOf(event.value.codeId) > -1 ? true : false;
        this.getSelectedDenominatorsId();
        if (isCodeAlreadySelected) {
          this.getDenominators().controls[index].patchValue({
            code: null
          });
          this.toasterService.showToast('error', 'Same code can not be selected again !!', 'EMCARE !!');
        }
      }
    }
  }
}

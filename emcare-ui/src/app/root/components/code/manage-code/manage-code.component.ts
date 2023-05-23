import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-manage-code',
  templateUrl: './manage-code.component.html',
  styleUrls: ['./manage-code.component.scss']
})
export class ManageCodeComponent implements OnInit {

  codeForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean;
  isAddFeature = true;
  isEditFeature = true;
  isAllowed = true;
  conditionArr = [
    { id: '=', name: '= (equal)' },
    { id: '<', name: '< (less than)' },
    { id: '>', name: '> (greater than)' },
    { id: '<=', name: '<= (less than equal to)' },
    { id: '>=', name: '>= (greater than equal to)' }
  ];
  valueTypeArr = [
    { id: 'any', name: 'Any' },
    { id: 'boolean', name: 'Boolean' },
    { id: 'text', name: 'String' },
    { id: 'date', name: 'Date' },
    { id: 'number', name: 'Number' }
  ];

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    this.checkEditParam();
    this.initCodeForm();
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
      this.fhirService.getCodeById(this.editId).subscribe(res => {
        if (res) {
          const obj = {
            code: res['code'],
            codeDescription: res['codeDescription']
          }
          this.codeForm.patchValue(obj);
        }
      });
    }
  }

  initCodeForm() {
    this.codeForm = this.formBuilder.group({
      code: ['', [Validators.required]],
      codeDescription: ['', [Validators.required]],
      customValueType: [''],
      customCodeCondition: [''],
      value: [''],
      valueArr: ['']
    });
  }

  get getFormConfrols() {
    return this.codeForm.controls;
  }

  getNameField(arr) {
    return arr.map(el => el.name);
  }

  saveData() {
    this.submitted = true;
    if (this.codeForm.valid) {
      if (this.isEdit) {
        const data = {
          "codeId": this.editId,
          "code": this.codeForm.get('code').value,
          "codeDescription": this.codeForm.get('codeDescription').value
        };
        this.fhirService.updateCustomCode(data).subscribe(() => {
          this.toasterService.showToast('success', 'Custom code updated successfully!', 'EM CARE');
          this.showCodeList();
        });
      } else {
        const data = {
          "code": this.codeForm.get('code').value,
          "codeDescription": this.codeForm.get('codeDescription').value,
          "valueType": this.getNameField([this.codeForm.get('customValueType').value])[0],
          "condition": this.getNameField(this.codeForm.get('customCodeCondition').value),
          "value": this.codeForm.get('valueArr').value
        };
        this.fhirService.addCustomCode(data).subscribe(() => {
          this.toasterService.showToast('success', 'Custom code added successfully!', 'EM CARE');
          this.showCodeList();
        });
      }
    }
  }

  showCodeList() {
    this.router.navigate([`code-list`]);
  }

  addValue() {
    const selValue = this.codeForm.get('value').value;
    const currValue = this.codeForm.get('valueArr').value;
    let valueArr = [];
    if (currValue) {
      valueArr = currValue;
      //  If value is selected already then no need to push it again
      if (!valueArr.includes(selValue)) {
        valueArr.push(selValue);
        this.codeForm.get('valueArr').setValue(valueArr);
      }
    } else {
      valueArr.push(selValue);
      this.codeForm.get('valueArr').setValue(valueArr);
    }
    this.codeForm.get('value').setValue(null);
  }

  removeValue(val) {
    let valueArr = this.codeForm.get('valueArr').value;
    valueArr = valueArr.filter(v => v !== val);
    this.codeForm.get('valueArr').setValue(valueArr);
  }
}

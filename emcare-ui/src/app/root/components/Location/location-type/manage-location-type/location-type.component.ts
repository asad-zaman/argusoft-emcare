import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LocationService } from 'src/app/root/services/location.service';

@Component({
  selector: 'app-location-type',
  templateUrl: './location-type.component.html',
  styleUrls: ['./location-type.component.scss']
})
export class LocationTypeComponent implements OnInit {

  locationTypeForm: FormGroup;
  isEdit: boolean;
  editId: string;

  constructor(
    private readonly formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private readonly locationService: LocationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initLocationTypeInputForm();
    this.checkEditParam();
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.locationService.getLocationTypeById(this.editId).subscribe(res => {
        if (res) {
          const obj = {
            type: res['code'],
            name: res['name']
          }
          this.locationTypeForm.setValue(obj);
        }
      });
    }
  }

  initLocationTypeInputForm() {
    this.locationTypeForm = this.formBuilder.group({
      type: ['', [Validators.required]],
      name: ['', [Validators.required]]
    });
  }

  saveData() {
    if (this.locationTypeForm.valid) {
      if (this.isEdit) {
        const data = {
          "hierarchyType": this.editId,
          "name": this.locationTypeForm.get('name').value,
          "code": this.locationTypeForm.get('type').value
        };
        this.locationService.updateLocationTypeById(data).subscribe(() => {
          this.showLocationType();
        });
      } else {
        const data = {
          "hierarchyType": this.locationTypeForm.get('name').value,
          "name": this.locationTypeForm.get('name').value,
          "code": this.locationTypeForm.get('type').value
        };
        this.locationService.createLocationType(data).subscribe((res) => {
          this.showLocationType();
        });
      }
    }
  }

  showLocationType() {
    this.router.navigate([`showLocationType`]);
  }
}

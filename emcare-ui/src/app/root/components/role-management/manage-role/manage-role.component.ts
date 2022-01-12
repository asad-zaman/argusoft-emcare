import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleManagementService } from 'src/app/root/services/role-management.service';

@Component({
  selector: 'app-manage-role',
  templateUrl: './manage-role.component.html',
  styleUrls: ['./manage-role.component.scss']
})
export class ManageRoleComponent implements OnInit {

  roleForm: FormGroup;
  isEdit: boolean;
  editId: string;
  oldRoleName: string;
  submitted: boolean;

  constructor(
    private readonly formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private readonly roleService: RoleManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initRoleInputForm();
    this.checkEditParam();
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.roleService.getRoleById(this.editId).subscribe(res => {
        if(res) {
          const obj = {
            name: res['name'],
            description: res['description']
          }
          this.oldRoleName = res['name'];
          this.roleForm.setValue(obj);
        }
      });
    }
  }

  initRoleInputForm() {
    this.roleForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      description: ['']
    });
  }

  get f() {
    return this.roleForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.roleForm.valid) {
      if (this.isEdit) {
        const data = {
          "id": this.editId,
          "name": this.roleForm.get('name').value,
          "oldRoleName": this.oldRoleName,
          "description": this.roleForm.get('description').value
        };
        this.roleService.updateRole(data).subscribe(() => {
          this.showRoles();
        });
      } else {
        const data = {
          "roleName": this.roleForm.get('name').value,
          "roleDescription": this.roleForm.get('description').value
        };
        this.roleService.createRole(data).subscribe((res) => {
          this.showRoles();
        });
      }
    }
  }

  showRoles() {
    this.router.navigate([`showRoles`]);
  }
}

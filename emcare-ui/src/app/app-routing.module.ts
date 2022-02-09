import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { HomeComponent } from './root/components/home/home.component';
import { ManageProfileComponent } from './root/components/manage-profile/manage-profile.component';
import { PatientListComponent } from './root/components/patient-management/patient-list/patient-list.component';
import { QuestionnaireBuilderComponent } from './root/components/questionnaire-management/questionnaire-builder/questionnaire-builder.component';
import { QuestionnaireListComponent } from './root/components/questionnaire-management/questionnaire-list/questionnaire-list.component';
import { ManageRoleComponent } from './root/components/role-management/manage-role/manage-role.component';
import { ShowRoleComponent } from './root/components/role-management/show-role/show-role.component';
import {
  LoginComponent,
  SignupComponent,
  // ManagePatientComponent,
  // ShowPatientComponent,
  // ManageOrganizationComponent,
  // ShowOrganizationComponent,
  LocationTypeComponent,
  LocationManagementComponent,
  ShowLocationComponent,
  ShowLocationTypeComponent,
  DeviceListComponent,
  UserListComponent,
  ManageUserComponent,
  ConfirmUserComponent,
  FeatureListComponent,
  ManageFeatureComponent,
  ManageTranslationsComponent
} from './root/index';

const routes: Routes = [
  { path: 'dashboard', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [AuthGuard] },
  // { path: 'addPatient', component: ManagePatientComponent, canActivate: [AuthGuard] },
  // { path: 'editPatient/:id', component: ManagePatientComponent, canActivate: [AuthGuard] },
  // { path: 'showPatient', component: ShowPatientComponent, canActivate: [AuthGuard] },
  // { path: 'addOrganization', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  // { path: 'editOrganization/:id', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  // { path: 'showOrganization', component: ShowOrganizationComponent, canActivate: [AuthGuard] },
  { path: 'addLocationType', component: LocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'editLocationType/:id', component: LocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'showLocationType', component: ShowLocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'addLocation', component: LocationManagementComponent, canActivate: [AuthGuard] },
  { path: 'editLocation/:id', component: LocationManagementComponent, canActivate: [AuthGuard] },
  { path: 'showLocation', component: ShowLocationComponent, canActivate: [AuthGuard] },
  { path: 'showDevices', component: DeviceListComponent, canActivate: [AuthGuard] },
  { path: 'showUsers', component: UserListComponent, canActivate: [AuthGuard] },
  { path: 'addUser', component: ManageUserComponent, canActivate: [AuthGuard] },
  { path: 'updateUser/:id', component: ManageUserComponent, canActivate: [AuthGuard] },
  { path: 'confirmUsers', component: ConfirmUserComponent, canActivate: [AuthGuard] },
  { path: 'showPatients', component: PatientListComponent, canActivate: [AuthGuard] },
  { path: 'showQuestionnaires', component: QuestionnaireListComponent, canActivate: [AuthGuard] },
  { path: 'addQuestionnaire', component: QuestionnaireBuilderComponent, canActivate: [AuthGuard] },
  { path: 'updateQuestionnaire/:id', component: QuestionnaireBuilderComponent, canActivate: [AuthGuard] },
  { path: 'showRoles', component: ShowRoleComponent, canActivate: [AuthGuard] },
  { path: 'addRole', component: ManageRoleComponent, canActivate: [AuthGuard] },
  { path: 'editRole/:id', component: ManageRoleComponent, canActivate: [AuthGuard] },
  { path: 'showFeatures', component: FeatureListComponent, canActivate: [AuthGuard] },
  { path: 'editFeature/:id', component: ManageFeatureComponent, canActivate: [AuthGuard] },
  { path: 'manage-translation', component: ManageTranslationsComponent },
  { path: 'editProfile', component: ManageProfileComponent },
  { path: '**', redirectTo: 'login' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

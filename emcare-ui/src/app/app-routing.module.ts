import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { AdminPanelComponent } from './root/components/admin-panel/admin-panel.component';
import { DuplicatePatientsComponent } from './root/components/duplicate-patients/duplicate-patients.component';
import { HomeComponent } from './root/components/home/home.component';
import {
  LoginComponent,
  SignupComponent,
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
  ManageTranslationsComponent,
  ComparePatientComponent,
  ManageFacilityComponent,
  ManageProfileComponent,
  PatientListComponent,
  QuestionnaireBuilderComponent,
  QuestionnaireListComponent,
  ManageRoleComponent,
  ShowRoleComponent,
  ShowFacilityComponent,
  LanguageListComponent,
  ForgotPasswordComponent,
  ManageOrganizationComponent,
  OrganizationListComponent
} from './root/index';

const routes: Routes = [
  { path: 'dashboard', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [AuthGuard] },
  { path: 'forgotPassword', component: ForgotPasswordComponent },
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
  { path: 'comparePatients', component: ComparePatientComponent, canActivate: [AuthGuard] },
  { path: 'showQuestionnaires', component: QuestionnaireListComponent, canActivate: [AuthGuard] },
  { path: 'addQuestionnaire', component: QuestionnaireBuilderComponent, canActivate: [AuthGuard] },
  { path: 'updateQuestionnaire/:id', component: QuestionnaireBuilderComponent, canActivate: [AuthGuard] },
  { path: 'showRoles', component: ShowRoleComponent, canActivate: [AuthGuard] },
  { path: 'addRole', component: ManageRoleComponent, canActivate: [AuthGuard] },
  { path: 'editRole/:id', component: ManageRoleComponent, canActivate: [AuthGuard] },
  { path: 'showFeatures', component: FeatureListComponent, canActivate: [AuthGuard] },
  { path: 'editFeature/:id', component: ManageFeatureComponent, canActivate: [AuthGuard] },
  { path: 'manage-language', component: ManageTranslationsComponent, canActivate: [AuthGuard] },
  { path: 'manage-language/:id', component: ManageTranslationsComponent, canActivate: [AuthGuard] },
  { path: 'editProfile', component: ManageProfileComponent },
  { path: 'addFacility', component: ManageFacilityComponent, canActivate: [AuthGuard] },
  { path: 'editFacility/:id', component: ManageFacilityComponent, canActivate: [AuthGuard] },
  { path: 'showFacility', component: ShowFacilityComponent, canActivate: [AuthGuard] },
  { path: 'language-list', component: LanguageListComponent, canActivate: [AuthGuard] },
  { path: 'user-admin-settings', component: AdminPanelComponent, canActivate: [AuthGuard] },
  { path: 'manage-organization', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  { path: 'manage-organization/:id', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  { path: 'showOrganizations', component: OrganizationListComponent, canActivate: [AuthGuard] },
  { path: 'duplicatePatients', component: DuplicatePatientsComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

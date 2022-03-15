import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
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
  ShowFacilityComponent
} from './root/index';

const routes: Routes = [
  { path: 'dashboard', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [AuthGuard] },
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
  { path: 'manage-translation', component: ManageTranslationsComponent, canActivate: [AuthGuard] },
  { path: 'editProfile', component: ManageProfileComponent },
  { path: 'manageFacility', component: ManageFacilityComponent, canActivate: [AuthGuard] },
  { path: 'manageFacility/:id', component: ManageFacilityComponent, canActivate: [AuthGuard] },
  { path: 'showFacility', component: ShowFacilityComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

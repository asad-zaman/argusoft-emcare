import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthenticationService } from '../shared/services/authentication.service';
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    user: any;
    result: boolean;
    featureRouteArr: Object;
    routeFeatureMapper: Object;

    private readonly handleFeature: BehaviorSubject<any>;

    constructor(
        private readonly router: Router,
        private readonly authService: AuthenticationService
    ) {
        this.handleFeature = new BehaviorSubject({});
        this.featureRouteArr = {
            'Users': '/showUsers',
            'Location Management': '/showLocation',
            'Patient Management': '/showPatients',
            'Feature Management': '/showFeatures',
            'Roles': '/showRoles',
            'Device Management': '/showDevices',
            'Questionnaire Management': '/showQuestionnaires',
            'Language Management': '/language-list',
            'Manage Facility': '/showFacility'
        }
        this.routeFeatureMapper = {
            'addLocationType': ['canAdd'], 'showLocationType': ['canView', 'canAdd', 'canEdit'],
            'addLocation': ['canAdd'], 'showLocation': ['canView', 'canAdd', 'canEdit'],
            'editLocationType': ['canEdit'], 'editLocation': ['canEdit'],
            'updateUser': ['canEdit'], 'updateQuestionnaire': ['canEdit'], 'editRole': ['canEdit'],
            'addFacility': ['canAdd'], 'editFacility': ['canEdit'], 'showDevices': ['canView'],
            'showUsers': ['canView', 'canAdd', 'canEdit'], 'addUser': ['canAdd'], 'confirmUsers': ['canView'],
            'showPatients': ['canView'], 'showQuestionnaires': ['canView', 'canAdd', 'canEdit'],
            'addQuestionnaire': ['canAdd'], 'showRoles': ['canView', 'canAdd', 'canEdit'],
            'addRole': ['canAdd'], 'showFeatures': ['canView', 'canEdit'],
            'manage-language': ['canEdit', 'canAdd'], 'editFeature': ['canAdd', 'canDelete'],
            'showFacility': ['canAdd', 'canEdit', 'canView', 'canDelete'], 'comparePatients': ['canAdd'],
            'language-list': ['canAdd', 'canEdit', 'canView']
        }
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
        const token = JSON.parse(localStorage.getItem('access_token'));
        const tokenExpiryDate = JSON.parse(localStorage.getItem('refresh_token_expiry_time'));
        const tokenExpiry = tokenExpiryDate
            ? new Date(tokenExpiryDate)
            : null;
        // Check if token is expired or not
        if (token) {
            if (tokenExpiry && tokenExpiry <= new Date()) {
                // token has expired user should benavigateByUrl logged out
                this.router.navigate(['/login']);
                localStorage.clear();
                return false;
            } else {
                this.authService.getLoggedInUser().subscribe(res => {
                    this.user = res;
                    this.getResultAndRedirect(route);
                    return true;
                });
                return true;
            }
        } else {
            if (route.routeConfig.path !== 'login' && route.routeConfig.path !== 'signup') {
                this.router.navigate(['/login']);
            }
            return true;
        }
    }

    getResultAndRedirect(route) {
        if (this.user.feature) {
            this.result = this.checkFeature(route);
            if (!this.result) {
                const route = this.getFeatureAndRedirectUser(this.user.feature[0].menuName);
                this.router.navigate([route]);
            } else {
                let featureJSON = this.getRouteAndFindFeature(route);
                featureJSON = JSON.parse(featureJSON);
                let tempRoute;
                if (route.routeConfig.path.includes('/:id')) {
                    tempRoute = route.routeConfig.path.split('/:id')[0];
                } else {
                    tempRoute = route.routeConfig.path;
                }
                const relatedFeature = this.routeFeatureMapper[tempRoute];
                const data = {
                    featureJSON: featureJSON,
                    relatedFeature: relatedFeature
                }
                this.handleFeature.next(data);
            }
        }
    }

    setFeatureData(data: any) {
        this.handleFeature.next(data);
    }

    getFeatureData(): Observable<any> {
        return this.handleFeature.asObservable();
    }

    getFeatureAndRedirectUser(feature) {
        return this.featureRouteArr[feature];
    }

    getRouteAndFindFeature(route) {
        let feature;
        if (route.routeConfig.path.includes('Location')) {
            feature = 'Location Management';
        } else if (route.routeConfig.path.includes('Devices')) {
            feature = 'Device Management';
        } else if (route.routeConfig.path.includes('User')) {
            feature = 'Users';
        } else if (route.routeConfig.path.includes('Patients')) {
            feature = 'Patient Management';
        } else if (route.routeConfig.path.includes('Questionnaire')) {
            feature = 'Questionnaire Management';
        } else if (route.routeConfig.path.includes('Role')) {
            feature = 'Roles';
        } else if (route.routeConfig.path.includes('Feature')) {
            feature = 'Feature Management';
        } else if (route.routeConfig.path.includes('language')) {
            feature = 'Language Management';
        } else if (route.routeConfig.path.includes('Facility')) {
            feature = 'Manage Facility';
        }
        return this.user.feature.find(f => f.menuName === feature).featureJson;
    }

    checkFeature(route) {
        if (
            route.routeConfig.path === 'addLocationType'
            || route.routeConfig.path.includes('editLocationType')
            || route.routeConfig.path === 'showLocationType'
            || route.routeConfig.path === 'addLocation'
            || route.routeConfig.path.includes('editLocation')
            || route.routeConfig.path === 'showLocation') {
            return !!this.user.feature.find(f => f.menuName === 'Location Management');
        } else if (
            route.routeConfig.path === 'showDevices'
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Device Management');
        } else if (
            route.routeConfig.path === 'showUsers'
            || route.routeConfig.path === 'addUser'
            || route.routeConfig.path.includes('updateUser')
            || route.routeConfig.path === 'confirmUsers'
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Users');
        } else if (
            route.routeConfig.path === 'showPatients'
            || route.routeConfig.path === 'comparePatients'
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Patient Management');
        } else if (
            route.routeConfig.path === 'showQuestionnaires'
            || route.routeConfig.path === 'addQuestionnaire'
            || route.routeConfig.path.includes('updateQuestionnaire')
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Questionnaire Management');
        } else if (
            route.routeConfig.path === 'showRoles'
            || route.routeConfig.path === 'addRole'
            || route.routeConfig.path.includes('editRole')
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Roles');
        } else if (
            route.routeConfig.path === 'showFeatures'
            || route.routeConfig.path.includes('editFeature')
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Feature Management');
        } else if (
            route.routeConfig.path.includes('manage-language')
            || route.routeConfig.path === 'language-list'
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Language Management');
        } else if (
            route.routeConfig.path === 'showFacility'
            || route.routeConfig.path === 'addFacility'
            || route.routeConfig.path.includes('editFacility')
        ) {
            return !!this.user.feature.find(f => f.menuName === 'Manage Facility');
        } else {
            return false;
        }
    }
}
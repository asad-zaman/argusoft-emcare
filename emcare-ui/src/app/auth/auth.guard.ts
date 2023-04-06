import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthenticationService } from '../shared/services/authentication.service';
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    user: any;
    result;
    featureRouteArr: Object;
    routeFeatureMapper: Object;

    private readonly handleFeature: BehaviorSubject<any>;

    constructor(
        private readonly router: Router,
        private readonly authService: AuthenticationService
    ) {
        this.handleFeature = new BehaviorSubject({});
        this.featureRouteArr = {
            'Users': ['/showUsers', '/addUser', '/updateUser', '/showRoles', '/addRole', '/editRole', '/confirmUsers'],
            'Indicators': ['/code-list', '/manageCode', 'indicator-list', '/addIndicator'],
            'Custom Codes': ['/code-list', '/manageCode'],
            'All Indicators': ['/indicator-list'],
            'Locations': ['/showFacility', '/addFacility', '/editFacility', '/showLocation', '/addLocation', '/editLocation', '/showLocationType', '/addLocationType', '/editLocationType'],
            'Patients': ['/showPatients', '/comparePatients', '/duplicatePatients', '/consultation-list', '/view-consultation'],
            'All Users': ['/showUsers', '/addUser', '/updateUser'],
            'Roles': ['/showRoles', '/addRole', '/editRole'],
            'Registration Request': ['/confirmUsers'],
            'Location Types': ['/showLocationType', '/addLocationType', '/editLocationType'],
            'Administrative Levels': ['/showLocation', '/addLocation', '/editLocation'],
            'All Patient': ['/showPatients'],
            'Compare Patients': ['/comparePatients'],
            'Features': ['/showFeatures', '/editFeature'],
            'Devices': ['/showDevices'],
            'Questionnaires': ['/showQuestionnaires', '/addQuestionnaire', '/updateQuestionnaire'],
            'Languages': ['/language-list', '/manage-language'],
            'Health facilities': ['/showFacility', '/addFacility', '/editFacility'],
            'User Settings': ['/user-admin-settings'],
            'Dashboard': ['/dashboard'],
            'Organizations': ['/showOrganizations', '/manage-organization'],
            'Duplicate Patients': ['/duplicatePatients'],
            'Advanced settings': ['/language-list', '/manage-language', '/user-admin-settings', '/showFeatures', '/editFeature', '/showDevices', '/logList'],
            'Consultations': ['/consultation-list', '/view-consultation'],
            'Application Logs': ['/logList', '/addLog']
        }
        this.routeFeatureMapper = {
            'logList': { f: 'Application Logs', reqFeature: ['canView'] },
            'addLog': { f: 'Application Logs', reqFeature: ['canAdd', 'canView'] },
            'addLocationType': { f: 'Location Types', reqFeature: ['canAdd'] },
            'showLocationType': { f: 'Location Types', reqFeature: ['canView', 'canAdd', 'canEdit'] },
            'addLocation': { f: 'Administrative Levels', reqFeature: ['canAdd'] },
            'showLocation': { f: 'Administrative Levels', reqFeature: ['canView', 'canAdd', 'canEdit'] },
            'editLocationType': { f: 'Location Types', reqFeature: ['canEdit'] },
            'editLocation': { f: 'Administrative Levels', reqFeature: ['canEdit'] },
            'updateUser': { f: 'All Users', reqFeature: ['canEdit'] },
            'updateQuestionnaire': { f: 'Questionnaires', reqFeature: ['canEdit'] },
            'editRole': { f: 'Roles', reqFeature: ['canEdit'] },
            'addFacility': { f: 'Health facilities', reqFeature: ['canAdd'] },
            'editFacility': { f: 'Health facilities', reqFeature: ['canEdit'] },
            'showDevices': { f: 'Devices', reqFeature: ['canView'] },
            'showUsers': { f: 'All Users', reqFeature: ['canView', 'canAdd', 'canEdit'] },
            'addUser': { f: 'All Users', reqFeature: ['canAdd'] },
            'confirmUsers': { f: 'Registration Request', reqFeature: ['canView'] },
            'showPatients': { f: 'Patients', reqFeature: ['canView'] },
            'showQuestionnaires': { f: 'Questionnaires', reqFeature: ['canView', 'canAdd', 'canEdit'] },
            'addQuestionnaire': { f: 'Questionnaires', reqFeature: ['canAdd'] },
            'showRoles': { f: 'Roles', reqFeature: ['canView', 'canAdd', 'canEdit'] },
            'addRole': { f: 'Roles', reqFeature: ['canAdd'] },
            'showFeatures': { f: 'Features', reqFeature: ['canView', 'canEdit'] },
            'manage-language': { f: 'Languages', reqFeature: ['canEdit', 'canAdd'] },
            'editFeature': { f: 'Features', reqFeature: ['canAdd', 'canDelete'] },
            'showFacility': { f: 'Health facilities', reqFeature: ['canAdd', 'canEdit', 'canView', 'canDelete'] },
            'comparePatients': { f: 'Compare Patients', reqFeature: ['canAdd'] },
            'language-list': { f: 'Languages', reqFeature: ['canAdd', 'canEdit', 'canView'] },
            'user-admin-settings': { f: 'User Settings', reqFeature: ['canEdit', 'canView'] },
            'dashboard': { f: 'Dashboard', reqFeature: ['canView'] },
            'showOrganizations': { f: 'Organizations', reqFeature: ['canAdd', 'canEdit', 'canView'] },
            'manage-organization': { f: 'Organizations', reqFeature: ['canAdd', 'canEdit'] },
            'duplicatePatients': { f: 'Patients', reqFeature: ['canView'] },
            'consultation-list': { f: 'Patients', reqFeature: ['canView'] },
            'view-consultation': { f: 'Patients', reqFeature: ['canView'] },
            'code-list': { f: 'Indicators', reqFeature: ['canView'] },
            'manageCode': { f: 'Indicators', reqFeature: ['canAdd', 'canEdit'] },
            'addIndicator': { f: 'Indicators', reqFeature: ['canAdd'] },
            'indicator-list': { f: 'Indicators', reqFeature: ['canView', 'canAdd'] }
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
                const isSuperAdmin = localStorage.getItem('isSuperAdmin') === 'true';
                if (!isSuperAdmin && (route.routeConfig.path === 'tenantList'
                    || route.routeConfig.path === 'manageTenant')) {
                    this.router.navigate(['/dashboard']);
                    return false;
                } else if (isSuperAdmin && (route.routeConfig.path === 'tenantList'
                    || route.routeConfig.path === 'manageTenant')) {
                    return true;
                } else {
                    const userFeatures = localStorage.getItem('userFeatures');
                    if (userFeatures) {
                        this.user = JSON.parse(userFeatures);
                        this.getResultAndRedirect(route);
                        return true;
                    }
                }
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
            //  this.result will be the feature if there is a feature assigned to user
            this.result = this.checkRouteIsAccessibleForUser(route);
            if (!this.result) {
                //  it will return route array so redirecting the user to the first route
                const route = this.getFeatureAndRedirectUser(this.user.feature[0].menuName)[0];
                this.router.navigate([route]);
            } else {
                //  finding featureJSON from feature and finding feature from route 
                let featureJSON = this.getRouteAndFindFeature(route);
                featureJSON = JSON.parse(featureJSON);
                //   finding route from route.routeConfig.path 
                let tempRoute;
                if (route.routeConfig.path.includes('/:id')) {
                    tempRoute = route.routeConfig.path.split('/:id')[0];
                } else {
                    tempRoute = route.routeConfig.path;
                }
                //  featureJSON is the feature which is assigned to the user
                //  relatedFeature is containing the feature that should be accessible for the given route
                const relatedFeature = this.routeFeatureMapper[tempRoute];
                const data = {
                    featureJSON: featureJSON,
                    relatedFeature: relatedFeature.reqFeature
                }
                this.setFeatureData(data);
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
        const currToute = route.routeConfig.path.split('/')[0]
        const feature = this.routeFeatureMapper[currToute].f;
        let fJSON = this.user.feature.find(f => f.menuName === feature);
        if (!fJSON) {
            let subFeature;
            this.user.feature.forEach(f => {
                if (f.subMenu.length > 0 && !subFeature) {
                    subFeature = f.subMenu.find(el => el.menuName === feature);
                }
            });
            return subFeature.featureJson;
        } else {
            return fJSON.featureJson;
        }
    }

    checkRouteIsAccessibleForUser(route) {
        if (
            route.routeConfig.path === 'addLocationType'
            || route.routeConfig.path.includes('editLocationType')
            || route.routeConfig.path === 'showLocationType'
            || route.routeConfig.path === 'addLocation'
            || route.routeConfig.path.includes('editLocation')
            || route.routeConfig.path === 'showLocation'
            || route.routeConfig.path === 'showFacility'
            || route.routeConfig.path === 'addFacility'
            || route.routeConfig.path.includes('editFacility')) {
            return this.user.feature.find(f => f.menuName === 'Locations');
        } else if (
            route.routeConfig.path === 'showUsers'
            || route.routeConfig.path === 'addUser'
            || route.routeConfig.path.includes('updateUser')
            || route.routeConfig.path === 'confirmUsers'
            || route.routeConfig.path === 'showRoles'
            || route.routeConfig.path === 'addRole'
            || route.routeConfig.path.includes('editRole')
        ) {
            return this.user.feature.find(f => f.menuName === 'Users');
        } else if (
            route.routeConfig.path === 'showPatients'
            || route.routeConfig.path === 'comparePatients'
            || route.routeConfig.path === 'duplicatePatients'
            || route.routeConfig.path === 'consultation-list'
            || route.routeConfig.path.includes('view-consultation')
        ) {
            return this.user.feature.find(f => f.menuName === 'Patients');
        } else if (
            route.routeConfig.path === 'showQuestionnaires'
            || route.routeConfig.path === 'addQuestionnaire'
            || route.routeConfig.path.includes('updateQuestionnaire')
        ) {
            return this.user.feature.find(f => f.menuName === 'Questionnaires');
        } else if (
            route.routeConfig.path === 'showFeatures'
            || route.routeConfig.path.includes('editFeature')
            || route.routeConfig.path === 'showDevices'
            || route.routeConfig.path.includes('manage-language')
            || route.routeConfig.path === 'language-list'
            || route.routeConfig.path === 'user-admin-settings'
            || route.routeConfig.path === 'logList'
            || route.routeConfig.path === 'addLog'
        ) {
            return this.user.feature.find(f => f.menuName === 'Advanced settings');
        } else if (
            route.routeConfig.path === 'showOrganizations'
            || route.routeConfig.path.includes('manage-organization')
        ) {
            return this.user.feature.find(f => f.menuName === 'Organizations');
        } else if (
            route.routeConfig.path === 'dashboard'
        ) {
            return this.user.feature.find(f => f.menuName === 'Dashboard');
        } else if (
            route.routeConfig.path === 'addIndicator'
            || route.routeConfig.path === 'code-list'
            || route.routeConfig.path === 'indicator-list'
            || route.routeConfig.path.includes('manageCode')
        ) {
            return this.user.feature.find(f => f.menuName === 'Indicators');
        } else {
            return false;
        }
    }
}
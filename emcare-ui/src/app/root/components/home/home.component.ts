import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import * as Highcharts from 'highcharts';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';
import { UserManagementService } from '../../services/user-management.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  dashboardData: any = {};
  isView = true;
  uniqueLocArr = [];
  userFacArr = [];

  @ViewChild('mapRef', { static: true }) mapElement: ElementRef;

  constructor(
    private readonly fhirService: FhirService,
    private readonly routeService: Router,
    private readonly authGuard: AuthGuard,
    private readonly userManagementService: UserManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.fhirService.getDashboardData().subscribe((res) => {
      this.dashboardData = res;
    });
    this.getAllUsers();
    this.getAllPatients();
    this.loadMap();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getAllUsers() {
    this.userManagementService.getAllUsers().subscribe((res: Array<any>) => {
      if (res) {
        res.forEach(el => {
          if (el.facilities && el.facilities.length > 0) {
            el.facilities.forEach(f => {
              const tempLoc = this.userFacArr.find(fac => fac.name == f.facilityName);
              if (tempLoc) {
                tempLoc.y += 1;
              } else {
                this.userFacArr.push({ name: f.facilityName, y: 1 });
              }
            });
          }
        });
        this.barChartPopulation();
      }
    });
  }

  barChartPopulation() {
    let locArr = this.userFacArr.map(l => l.name);
    let locDataArr = this.userFacArr.map(l => l.y);
    Highcharts.chart('barChart', {
      chart: {
        type: 'bar'
      },
      title: {
        text: 'Users per Facility'
      },
      xAxis: {
        categories: locArr,
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Users ',
          align: 'high'
        },
      },
      tooltip: {
        valueSuffix: ''
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      series: [{
        type: undefined,
        name: 'Current Users',
        data: locDataArr
      }]
    });
  }

  getAllPatients() {
    this.fhirService.getAllPatients().subscribe((res: Array<any>) => {
      if (res) {
        res.forEach(el => {
          const tempLoc = this.uniqueLocArr.find(l => l.name == el.location);
          if (tempLoc) {
            tempLoc.y += 1;
          } else {
            this.uniqueLocArr.push({ name: el.location, y: 1 });
          }
        });
        this.pieChartBrowser()
      }
    });
  }

  pieChartBrowser() {
    Highcharts.chart('pieChart', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: 'Patients in different locations'
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: true,
            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
          }
        }
      },
      series: [{
        name: 'Location',
        colorByPoint: true,
        type: undefined,
        data: this.uniqueLocArr
      }]
    });
  }

  loadMap = () => {
    const positions = { lat: 33.2232, lng: 43.6793 };
    const map = new window['google'].maps.Map(this.mapElement.nativeElement, {
      center: positions, zoom: 7
    });

    const marker = new window['google'].maps.Marker({
      position: positions,
      map: map,
      title: 'Map!',
      draggable: true,
      animation: window['google'].maps.Animation.DROP,
    });

    const contentString = '<div id="content">' +
      '<div id="siteNotice">' +
      '</div>' +
      '<h3 id="thirdHeading" class="thirdHeading">Iraq</h3>' +
      '<div id="bodyContent">' +
      '<p>Welcome to Iraq</p>' +
      '</div>' +
      '</div>';

    const infowindow = new window['google'].maps.InfoWindow({
      content: contentString
    });

    marker.addListener('click', function () {
      infowindow.open(map, marker);
    });
  }

  redirectToRoute(route: string) {
    this.routeService.navigate([route]);
  }
}
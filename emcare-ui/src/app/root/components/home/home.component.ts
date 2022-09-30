import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import * as Highcharts from 'highcharts';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  dashboardData: any = {};
  isView = true;
  uniqueLocArr = [];
  userFacObj = {};
  facilityArr = [];

  @ViewChild('mapRef', { static: true }) mapElement: ElementRef;

  constructor(
    private readonly fhirService: FhirService,
    private readonly routeService: Router,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.fhirService.getDashboardData().subscribe((res) => {
      this.dashboardData = res;
    });
    this.getChartData();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  barChartPopulation() {
    let locArr = this.userFacObj['names'];
    let locDataArr = this.userFacObj['count'];
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

  getChartData() {
    this.fhirService.getChartData().subscribe((res: Array<any>) => {
      if (res) {
        res['pieChart'].map(el => {
          this.uniqueLocArr.push({ name: el['name'], y: el['count'] });
        });
        this.userFacObj = {
          count: res['barChart']['counts'],
          names: res['barChart']['names']
        }
        this.manipulateMapView(res['mapView']);
        this.barChartPopulation();
        this.pieChartBrowser();
        this.loadMap();
      }
    });
  }

  manipulateMapView(data) {
    data.map(d => {
      this.facilityArr.push({
        name: d.facilityName,
        positions: { lat: Number(d.latitude), lng: Number(d.longitude) }
      });
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
    let markers = [];
    const locationArr = this.facilityArr.map(d => d.positions);
    const centerPosition = this.facilityArr[0]['positions'];
    const map = new window['google'].maps.Map(this.mapElement.nativeElement, {
      center: centerPosition, zoom: 4
    });

    this.facilityArr.forEach(data => {
      const marker = new window['google'].maps.Marker({
        position: new window['google'].maps.LatLng(data['positions'].lat, data['positions'].lng),
        map: map,
        title: 'Map!',
        draggable: true,
        animation: window['google'].maps.Animation.DROP
      });
      const contentString = '<div id="content">' +
        '<div id="siteNotice">' +
        '</div>' +
        `<h3 id="thirdHeading" class="thirdHeading">${data['name']}</h3>` +
        '<div id="bodyContent">' +
        '</div>' +
        '</div>';
      const infowindow = new window['google'].maps.InfoWindow({
        content: contentString
      });
      markers.push({ marker: marker, infowindow: infowindow });
    });

    markers.forEach(data => {
      data.marker.addListener('click', function () {
        data.infowindow.open(map, data.marker);
      });
    })
  }

  redirectToRoute(route: string) {
    this.routeService.navigate([route]);
  }

  isEmpty(obj) {
    return Object.keys(obj).length === 0;
  }

  getDateData() {
    const monthArr = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    const d = new Date();
    let month = monthArr[d.getMonth()];
    let year = d.getFullYear();
    return `${month} ${year}`;
  }
}
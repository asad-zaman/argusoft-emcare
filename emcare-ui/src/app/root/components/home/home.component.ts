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
  lastScDate = `${new Date().toDateString()} ${new Date().toLocaleTimeString()}`;

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
    this.getDashboardData();
    this.getChartData();
  }

  getDashboardData() {
    this.fhirService.getDashboardData().subscribe((res) => {
      this.dashboardData = res;
    });
  }

  syncApis() {
    this.getDashboardData();
    this.getChartData();
  }

  getLastSyncDate() {
    this.lastScDate = `${new Date().toDateString()} ${new Date().toLocaleTimeString()}`;
    this.syncApis();
    return this.lastScDate;
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
    //  toDo  temparory creating arrays for scatter plot
    let locDataArr = [];
    this.userFacObj['count'].forEach(d => { locDataArr.push([d, d]); });
    let options = {
      chart: {
        type: 'scatter',
        margin: [70, 50, 60, 80],
        events: {
          click: function (e) {
            // find the clicked values and the series
            let x = Math.round(e.xAxis[0].value);
            let y = Math.round(e.yAxis[0].value);
            // Add it
            // let series = this.series[0];
            // series.addPoint([x, y]);
          }
        }
      },
      title: {
        text: 'User supplied data'
      },
      subtitle: {
        text: 'Click the plot area to add a point. Click a point to remove it.'
      },
      accessibility: {
        announceNewData: {
          enabled: true
        }
      },
      xAxis: {
        gridLineWidth: 1,
        minPadding: 0.2,
        maxPadding: 0.2,
        maxZoom: 6
      },
      yAxis: {
        title: {
          text: 'Value'
        },
        minPadding: 0.2,
        maxPadding: 0.2,
        maxZoom: 6,
        plotLines: [{
          value: 0,
          width: 1,
          color: '#808080'
        }]
      },
      legend: {
        enabled: false
      },
      exporting: {
        enabled: false
      },
      plotOptions: {
        series: {
          lineWidth: 1,
          point: {
            events: {
              click: function () {
                if (this.series.data.length > 1) {
                  this.remove();
                }
              }
            }
          }
        }
      },
      series: [
        {
          type: undefined,
          data: locDataArr
        }
      ]
    }
    Highcharts.chart('container', options);
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
        this.firstPieChart();
        this.secondPieChart();
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

  firstPieChart() {
    Highcharts.chart('firstPieChart', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: 'Number of consultations per district'
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: false,
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

  secondPieChart() {
    Highcharts.chart('secondPieChart', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: 'Number of consultations by age group'
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: false,
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
      center: centerPosition, zoom: 5
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
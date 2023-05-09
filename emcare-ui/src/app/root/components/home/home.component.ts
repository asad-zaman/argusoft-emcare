import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import * as Highcharts from 'highcharts';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
import { default as NoData } from 'highcharts/modules/no-data-to-display';
import { appConstants } from 'src/app/app.config';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
NoData(Highcharts);

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  dashboardData: any = {};
  isView = true;
  facilityArr = [];
  lastScDate = `${new Date().toDateString()} ${new Date().toLocaleTimeString()}`;
  indicatorArr = [];
  scatterData = [];
  consultationPerFacility = [];
  consultationByAgeGroup = [];
  indicatorApiBusy = true;
  genderArr = appConstants.genderArr;
  conditionArrForAgeAndColor = appConstants.conditionArrForAgeAndColor;
  indicatorFilterForm: FormGroup;

  @ViewChild('mapRef', { static: true }) mapElement: ElementRef;

  constructor(
    private readonly fhirService: FhirService,
    private readonly routeService: Router,
    private readonly authGuard: AuthGuard,
    private readonly cdr: ChangeDetectorRef,
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  ngAfterViewInit() {
    this.cdr.detectChanges();
  }

  prerequisite() {
    this.checkFeatures();
    this.getDashboardData();
    this.getChartData();
    this.getIndicatorCompileValue();
    this.initIndicatorFilterForm();
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

  scatterChart() {
    let options = {
      chart: {
        type: 'scatter',
        margin: [70, 50, 60, 80],
        events: {
          click: function (e) {
            let x = Math.round(e.xAxis[0].value);
            let y = Math.round(e.yAxis[0].value);
          }
        }
      },
      title: {
        text: undefined
      },
      accessibility: {
        announceNewData: {
          enabled: true
        }
      },
      tooltip: {
        enabled: true,
        headerFormat: undefined,
        pointFormat: `<b>Date = {point.d}</b>, <b>Week = {point.week}</b>, <b>Consultations = {point.y}</b>`,
      },
      xAxis: {
        labels: {
          format: '{value:%e-%b-%Y}'
        },
        title: {
          text: undefined,
        },
        gridLineWidth: 1,
        minPadding: 0.2,
        maxPadding: 0.2,
        maxZoom: 6
      },
      yAxis: {
        title: {
          text: undefined,
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
      credits: {
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
                  // this.remove();
                }
              }
            }
          }
        }
      },
      series: [
        {
          type: undefined,
          data: this.scatterData
        }
      ]
    }
    Highcharts.chart('scatter-chart-container', options);
  }

  getChartData() {
    this.fhirService.getChartData().subscribe((res: Array<any>) => {
      if (res) {
        //  for scatter plot
        res['scatterChart'].forEach((el, index) => {
          this.scatterData.push({
            x: new Date(el[2]),
            y: el[1],
            week: el[0],
            d: new Date(el[2]).toLocaleDateString()
          });
        });
        //  for first pie chart
        this.consultationPerFacility = res['consultationPerFacility'];
        this.consultationPerFacility.forEach(el => {
          el['y'] = el['count'];
        });
        let key;
        for (key in res['consultationByAgeGroup']) {
          if (res['consultationByAgeGroup'].hasOwnProperty(key)) {
            //  for second pie chart
            this.consultationByAgeGroup.push({
              name: key, y: res['consultationByAgeGroup'][key]
            });
          }
        }
        this.manipulateMapView(res['mapView']);
        this.scatterChart();
        this.consultationPerFacilityChart();
        this.consultationByAgeGroupChart();
      }
    });
  }

  manipulateMapView(data) {
    data.map(d => {
      this.facilityArr.push({
        id: d.facilityId,
        organizationName: d.organizationName,
        name: d.facilityName,
        positions: { lat: Number(d.latitude), lng: Number(d.longitude) }
      });
    });
    this.loadMap();
  }

  consultationPerFacilityChart() {
    Highcharts.chart('consultationPerFacility', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: ''
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          size: '90%',
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
        data: this.consultationPerFacility
      }]
    });
  }

  consultationByAgeGroupChart() {
    Highcharts.chart('consultationByAgeGroup', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: ''
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          size: '90%',
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
        data: this.consultationByAgeGroup
      }]
    });
  }

  loadMap = () => {
    let markers = [];
    const centerPosition = { lat: 33.2232, lng: 43.6793 };
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
      data.marker.addListener('mouseover', function () {
        data.infowindow.open(map, data.marker);
      });
      data.marker.addListener('mouseout', function () {
        data.infowindow.close();
      });
    });
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

  getIndicatorCompileValue() {
    const codeArr = [3843];
    this.fhirService.getIndicatorCompileValue(codeArr).subscribe((res: any) => {
      if (res && res.length > 0) {
        this.indicatorArr = res;
        this.indicatorApiBusy = false;
        this.indicatorArr.forEach(el => {
          el['showFilter'] = false;
        });
      }
    }, () => {
      this.indicatorApiBusy = false;
    });
  }

  initIndicatorFilterForm() {
    this.indicatorFilterForm = this.formBuilder.group({
      gender: ['', []],
      ageCondition: ['', []],
      ageValue: ['', []],
      startDate: ['', []],
      endDate: ['', []],
      facility: ['', []]
    });
  }

  get getFormConfrols() {
    return this.indicatorFilterForm.controls;
  }

  enableFilter(index) {
    this.indicatorFilterForm.reset();
    this.indicatorArr.forEach((el, i) => {
      if (i !== index) {
        el['showFilter'] = false;
      }
    });
    this.indicatorArr[index].showFilter = !this.indicatorArr[index].showFilter;
  }

  filterIndicator(indicator) {
    if (this.getFormConfrols.gender.value ||
      this.getFormConfrols.startDate.value ||
      this.getFormConfrols.endDate.value ||
      (this.getFormConfrols.ageCondition.value && this.getFormConfrols.ageValue.value)
    ) {
      const data = {
        indicatorId: indicator.indicatorId,
        gender: this.getFormConfrols.gender.value ? this.getFormConfrols.gender.value.id : null,
        age: `${this.getFormConfrols.ageCondition.value.id} ${this.getFormConfrols.ageValue.value}`,
        startDate: new Date(this.getFormConfrols.startDate.value).toISOString(),
        endDate: this.getFormConfrols.endDate.value ? new Date(this.getFormConfrols.endDate.value).toISOString() : null,
      }
      this.fhirService.filterIndicatorValue(data).subscribe(res => {
        console.log(res);
      });
    } else {
      this.toasterService.showToast('warn', 'Please enter filter data!', 'EM CARE!');
    }
  }

  onDateSelection(num) {
    if (this.getFormConfrols.startDate.value && this.getFormConfrols.endDate.value) {
      const startDate = new Date(this.getFormConfrols.startDate.value).getTime();
      const endDate = new Date(this.getFormConfrols.endDate.value).getTime();
      if (endDate < startDate) {
        this.toasterService.showToast('error', 'End Date shoyld be greater than start date!', 'EM CARE!');
        num === 1 ? this.getFormConfrols.startDate.setValue(null) : this.getFormConfrols.endDate.setValue(null);
      }
    }
  }
}
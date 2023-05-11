import { ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { Router } from '@angular/router';
import * as Highcharts from 'highcharts';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
import { default as NoData } from 'highcharts/modules/no-data-to-display';
import { appConstants } from 'src/app/app.config';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import countryJSON from '../../../../assets/country.json';
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
  @ViewChildren('iValues') iValues: QueryList<ElementRef>;

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
  }

  getDashboardData() {
    this.fhirService.getDashboardData().subscribe((res) => {
      this.dashboardData = res;
    });
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
    // by default Iraq
    let country = localStorage.getItem(appConstants.localStorageKeys.ApplicationAgent);
    country = country === 'Global' ? 'Iraq' : country;

    const currCountry = countryJSON.filter(c => c.CountryName === country);
    const centerOfCountry = {
      lat: parseFloat(currCountry[0].CapitalLatitude),
      lng: parseFloat(currCountry[0].CapitalLongitude)
    };

    let markers = [];
    const centerPosition = { lat: centerOfCountry.lat, lng: centerOfCountry.lng };
    const map = new window['google'].maps.Map(this.mapElement.nativeElement, {
      center: centerPosition, zoom: 5
    });

    this.facilityArr.forEach(data => {
      const marker = new window['google'].maps.Marker({
        position: new window['google'].maps.LatLng(data['positions'].lat, data['positions'].lng),
        map: map,
        title: 'Map!',
        draggable: false,
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

  getIndicatorCompileValue() {
    const codeArr = [3843];
    this.fhirService.getIndicatorCompileValue(codeArr).subscribe((res: any) => {
      this.indicatorApiBusy = false;
      this.initIndicatorFilterForm();
      if (res && res.length > 0) {
        this.indicatorArr = res;
        this.indicatorArr.forEach(el => {
          const indicatorValue = el.indicatorValue;
          const colorSchema = el['colorSchema'] !== null ? JSON.parse(el['colorSchema']) : [];
          if (colorSchema.length === 0 || parseInt(indicatorValue) === 0) {
            // default colot
            el['color'] = "green";
          } else {
            colorSchema.forEach(cel => {
              if (cel.minValue < indicatorValue && indicatorValue < cel.maxValue) {
                el['color'] = cel.color;
              } else {
                if (!Object(el).hasOwnProperty('color')) {
                  // when range is not applied in any color scheme
                  el['color'] = 'black';
                }
              }
            });
          }
          el['showFilter'] = false;
          this.getIndicators().push(this.newIndicatorAddition(el));
        });
        this.iValues.changes.subscribe(c => {
          c.toArray().forEach((item, i) => {
            item.nativeElement.style.color = this.indicatorArr[i].color;
          });
        });
      }
    }, () => {
      this.indicatorApiBusy = false;
    });
  }

  initIndicatorFilterForm() {
    this.indicatorFilterForm = this.formBuilder.group({
      indicators: this.formBuilder.array([])
    });
  }

  getIndicators(): FormArray {
    return this.indicatorFilterForm.get("indicators") as FormArray;
  }

  newIndicatorAddition(data): FormGroup {
    return this.formBuilder.group({
      indicatorId: data.indicatorId,
      indicatorName: data.indicatorName,
      indicatorValue: data.indicatorValue,
      gender: data.gender ? this.genderArr.find(el => el.id === data.gender) : null,
      ageCondition: data.age ? this.fhirService.getAgeConditionAndValue(data.age).condition : null,
      ageValue: data.age ? this.fhirService.getAgeConditionAndValue(data.age).value : null,
      startDate: data.startDate,
      endDate: data.endDate,
      facility: []
    });
  }

  enableFilter(index) {
    this.indicatorArr.forEach((el, i) => {
      if (i !== index) {
        el['showFilter'] = false;
      }
    });
    this.indicatorArr[index].showFilter = !this.indicatorArr[index].showFilter;
  }

  filterIndicator(index) {
    const controls = this.getIndicators().controls[index];
    if (controls.value.gender || controls.value.startDate ||
      controls.value.endDate || (controls.value.ageCondition && controls.value.ageValue)
    ) {
      const data = {
        indicatorId: controls.value.indicatorId,
        gender: controls.value.gender ? controls.value.gender.id : null,
        age: controls.value.ageCondition && controls.value.ageValue ?
          `${controls.value.ageCondition.id} ${controls.value.ageValue}` : null,
        startDate: new Date(controls.value.startDate).toISOString(),
        endDate: controls.value.endDate ? new Date(controls.value.endDate).toISOString() : null,
      }
      this.fhirService.filterIndicatorValue(data).subscribe(res => {
        if (res) {
          controls.patchValue({ indicatorValue: res[0].indicatorValue });
        }
      });
    } else {
      this.toasterService.showToast('warn', 'Please enter filter data!', 'EM CARE!');
    }
  }

  onDateSelection(num, index) {
    const controls = this.getIndicators().controls[index];
    if (controls.value.startDate && controls.value.endDate) {
      const startDate = new Date(controls.value.startDate).getTime();
      const endDate = new Date(controls.value.endDate).getTime();
      if (endDate < startDate) {
        this.toasterService.showToast('error', 'End Date shoyld be greater than start date!', 'EM CARE!');
        num === 1 ? controls['controls'].startDate.setValue(null) : controls['controls'].endDate.setValue(null);
      }
    }
  }
}
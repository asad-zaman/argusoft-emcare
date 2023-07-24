import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
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
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {

  dashboardData: any = {};
  isView = true;
  facilityArr = [];
  lastScDate = `${new Date().toDateString()} ${new Date().toLocaleTimeString()}`;
  indicatorArr: any = [];
  scatterData = [];
  consultationPerFacility = [];
  consultationByAgeGroup = [];
  indicatorApiBusy = true;
  genderArr = appConstants.genderArr;
  conditionArrForAgeAndColor = appConstants.conditionArrForAgeAndColor;
  indicatorFilterForm: FormGroup;
  showChartData = false;
  consultationPerFacilityObj = {};
  consultationByAgeGroupObj = {};
  scatterDataObj = {};
  filterBarChartObj = {};
  FacilityName: String;
  month = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

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
    if (!this.conditionArrForAgeAndColor.find(el => el.id === 'bw')) {
      this.conditionArrForAgeAndColor.push({ id: 'bw', name: 'between' });
    }
    const userFacilityName = JSON.parse(localStorage.getItem(appConstants.localStorageKeys.FacilityName));
    this.FacilityName = userFacilityName.length == 1 ?
      userFacilityName[0] : `${userFacilityName[0]} and ${userFacilityName.length - 1}  more`;
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

  barChart() {
    this.scatterDataObj = {
      basicData: {
        labels: this.scatterData.map(el => el.d),
        datasets: [{ barThickness: 10, data: this.scatterData.map(el => el.y), borderWidth: 0 }]
      },
      basicOptions: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    }
  }

  getChartData() {
    this.fhirService.getChartData().subscribe((res: Array<any>) => {
      if (res) {
        //  for bar plot
        res['scatterChart'].forEach((el, index) => {
          const date = new Date('May 31, 2023');
          const mlDate = date.getTime();

          if (mlDate <= el[1]) {
            this.scatterData.push({
              x: new Date(el[1]),
              y: el[0],
              d: new Date(el[1]).toLocaleDateString(),
            });
          }
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
        this.barChart();
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
    this.consultationPerFacilityObj = {
      pieData: {
        labels: this.consultationPerFacility.map(el => el.name),
        datasets: [{ data: this.consultationPerFacility.map(el => el.y), }]
      },
      pieOptions: { plugins: { legend: { display: false, labels: { usePointStyle: false } } } }
    }
  }

  consultationByAgeGroupChart() {
    this.consultationByAgeGroupObj = {
      pieData: {
        labels: this.consultationByAgeGroup.map(el => el.name),
        datasets: [
          {
            data: this.consultationByAgeGroup.map(el => el.y),
            backgroundColor: ['#BDEBEC', '#5DC2C1'], hoverBackgroundColor: ['#BDEBEC', '#5DC2C1']
          }
        ]
      },
      pieOptions: { plugins: { legend: { display: false, labels: { usePointStyle: false } } } }
    }
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
    const codeArr = [];
    this.fhirService.getIndicatorCompileValue(codeArr).subscribe((res: any) => {
      this.indicatorApiBusy = false;
      this.initIndicatorFilterForm();
      if (res && res.length > 0) {
        this.indicatorArr = res;
        this.indicatorArr.forEach(el => {
          el.facilityIds = this.FacilityName;
          el.startDate = this.getDateFormat(el.startDate);
          el.endDate = this.getDateFormat(el.endDate);
          const indicatorValue = this.getIndicatorPercentageValue(el.chartData);
          el.indicatorValue = indicatorValue;
          this.setElementColor(el, indicatorValue);
          el['showFilter'] = false;
          el['showChart'] = false;
          this.getIndicators().push(this.newIndicatorAddition(el));
        });
        this.setColorStyle();
      }
    }, () => {
      this.indicatorApiBusy = false;
    });
  }

  setColorStyle() {
    this.iValues.changes.subscribe(c => {
      c.toArray().forEach((item, i) => {
        item.nativeElement.style.color = this.indicatorArr[i].color;
      });
    });
  }

  setElementColor(el, indicatorValue) {
    //  color swction
    const colorSchema = el['colorSchema'] !== null ? JSON.parse(el['colorSchema']) : [];
    if (colorSchema.length === 0 || indicatorValue === 0) {
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
  }

  getIndicatorPercentageValue(chartData) {
    if (chartData.length !== 0) {
      let numerator = 0;
      const denominator = chartData[0].count;
      chartData.forEach(el => numerator += el.patientwithcondition);
      //  percentage
      return (numerator * 100 / denominator).toFixed(2);
    } else {
      return 0;
    }
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
      facility: [],
      isShowBetween: false,
      ageExtraValue: ''
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

  enableChart(index) {
    this.indicatorArr.forEach((el, i) => {
      if (i !== index) {
        el['showChart'] = false;
      }
    });
    this.indicatorArr[index].showChart = !this.indicatorArr[index].showChart;
    //  preparing each chart
    this.filterBarChartObj = {
      basicData: {
        labels: this.indicatorArr[index].chartData.map(el => new Date(el.admissiondate).toLocaleDateString()),
        datasets: [{
          barThickness: 10,
          data: this.indicatorArr[index].chartData.map(el => el.patientwithcondition),
          borderWidth: 0
        }]
      },
      basicOptions: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    }
  }

  filterIndicator(index) {
    const controls = this.getIndicators().controls[index];
    const data = {
      facilityIds: controls.value.facility.map(el => el.id),
      indicatorId: controls.value.indicatorId,
      gender: controls.value.gender ? controls.value.gender.id : null,
      age: controls.value.isShowBetween ? (
        controls.value.ageCondition && controls.value.ageValue && controls.value.ageExtraValue ?
          `between ${controls.value.ageValue} and ${controls.value.ageExtraValue}` : null
      ) : (
        controls.value.ageCondition && controls.value.ageValue ?
          `${controls.value.ageCondition.id} ${controls.value.ageValue}` : null
      ),
      startDate: new Date(controls.value.startDate).toISOString(),
      endDate: controls.value.endDate ? new Date(controls.value.endDate).toISOString() : null,
    }
    this.fhirService.filterIndicatorValue(data).subscribe(res => {
      if (res) {
        const indicatorPercentage = this.getIndicatorPercentageValue(res[0].chartData);
        controls.patchValue({ indicatorValue: indicatorPercentage });
        const indicatorInfo = res[0];
        this.setElementColor(indicatorInfo, indicatorPercentage);
        const selectedFacilities = controls.value.facility ? controls.value.facility : null;
        if (selectedFacilities) {
          indicatorInfo.facilityIds = selectedFacilities.length == 1 ?
            selectedFacilities[0].name : selectedFacilities[0].name + " and " + (selectedFacilities.length - 1) + " more";
        } else {
          indicatorInfo.facilityIds = null;
        }
        indicatorInfo.startDate = this.getDateFormat(indicatorInfo.startDate);
        indicatorInfo.endDate = this.getDateFormat(indicatorInfo.endDate);
        //  saving information
        const index = this.indicatorArr.findIndex(el => el.indicatorId === indicatorInfo.indicatorId);
        this.indicatorArr[index] = indicatorInfo;
        this.setColorStyle();
      }
    });
  }

  onDateSelection(num, index) {
    const controls = this.getIndicators().controls[index];
    if (controls.value.startDate && controls.value.endDate) {
      const startDate = new Date(controls.value.startDate).getTime();
      const endDate = new Date(controls.value.endDate).getTime();
      if (endDate < startDate) {
        this.toasterService.showToast('error', 'End Date should be greater than start date!', 'EM CARE!');
        num === 1 ? controls['controls'].startDate.setValue(null) : controls['controls'].endDate.setValue(null);
      }
    }
  }

  checkForInBetween(event, i) {
    if (event.value && event.value.id === 'bw') {
      this.getIndicators().controls[i].patchValue({ isShowBetween: true });
    } else {
      this.getIndicators().controls[i].patchValue({ isShowBetween: false });
    }
  }

  getDateFormat(seconds: number) {
    const dateFormat = new Date(seconds);
    return `${dateFormat.getDate()} ${this.month[dateFormat.getMonth()]} ${dateFormat.getFullYear()}`;
  }
}
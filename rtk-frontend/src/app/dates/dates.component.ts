import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-dates',
  templateUrl: './dates.component.html',
  styleUrls: ['./dates.component.scss']
})
export class DatesComponent implements OnInit {
  datesForm: FormGroup;

  constructor() {
    this.datesForm = new FormGroup({
      beginDate: new FormControl(''),
      endDate: new FormControl(''),
    });
  }

  ngOnInit() {
    let dPBeginDate = new DatePipe(navigator.language);
    let dPEndDate = new DatePipe(navigator.language);

    let p = 'yyyy-MM-dd';

    let dTBeginDate = dPBeginDate.transform(new Date(), p);

    const d = new Date();
    const year = d.getFullYear();
    const month = d.getMonth();
    const day = d.getDate();
    const c = new Date(year + 1, month, day);

    let dTEndDate = dPEndDate.transform(c, p);

    this.datesForm.setValue({beginDate: dTBeginDate, endDate: dTEndDate});
  }

  public getBeginDate() {
    return this.datesForm.get("beginDate").value;
  }

  public getEndDate() {
    return this.datesForm.get("endDate").value;
  }
}

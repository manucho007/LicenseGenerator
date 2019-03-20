import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormArray,
  Validators
} from '@angular/forms';
// this.form = new FormGroup({
//   skills: new FormArray([new FormControl(true), new FormControl(false)])
// });
@Component({
  selector: 'app-generator',
  templateUrl: './generator.component.html',
  styleUrls: ['./generator.component.scss']
})
export class GeneratorComponent implements OnInit {
  form;
  user = {
    skills: [
      { name: 'JS', selected: true, id: 12 },
      { name: 'CSS', selected: false, id: 2 }
    ]
  };
  ngOnInit() {}


  constructor(
    private fb: FormBuilder
    ) {
    console.clear();

    this.form = this.fb.group({
      skills: this.buildSkills()
    });

    console.log(this.form.get('skills'));
  }

  get skills(): FormArray {
    return this.form.get('skills') as FormArray;
  }

  buildSkills() {
    const arr = this.user.skills.map(skill => {
      return this.fb.control(skill.selected);
    });
    return this.fb.array(arr);
  }

  submit(value) {
    const f = Object.assign({}, value, {
      skills: value.skills.map((s, i) => {
        return {
          id: this.user.skills[i].id,
          selected: s
        };
      })
    });

    console.log(f);
  }
}

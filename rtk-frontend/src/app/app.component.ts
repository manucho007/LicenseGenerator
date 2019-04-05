import {Component, OnInit, ViewChild} from '@angular/core';
import {RestService} from "./services/rest.service";
import {ITreeOptions, TreeComponent} from "angular-tree-component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(private rest: RestService) {}

  ngOnInit() {
    this.loadObjects();
  }

  loadObjects() {
    return this.rest.getObjects().subscribe((data: {}) => {
      const self = this;

      Object.keys(data).map(function(objectKeyElement) {
        const mapKeys = data[objectKeyElement];

        Object.keys(mapKeys).map(function(mapKeyElement) {
          const value = mapKeys[mapKeyElement];
          self.nodes.push(value);
        });
      });

      this.tree.treeModel.update();
    });
  }

  @ViewChild(TreeComponent)
  private tree: TreeComponent;

  title = 'rtk-frontend';

  nodes = [];

  options: ITreeOptions = {
    displayField: 'data',
    childrenField: 'children',
    useCheckbox: true
  };
}

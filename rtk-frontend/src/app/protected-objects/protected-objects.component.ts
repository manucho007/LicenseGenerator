import {Component, OnInit, ViewChild} from '@angular/core';
import {ITreeOptions, TreeComponent} from "angular-tree-component";
import {RestService} from "../services/rest.service";

@Component({
  selector: 'app-protected-objects',
  templateUrl: './protected-objects.component.html',
  styleUrls: ['./protected-objects.component.scss']
})
export class ProtectedObjectsComponent implements OnInit {

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

  public getTreeComponent() {
    return this.tree;
  }

  nodes = [];

  options: ITreeOptions = {
    displayField: 'data',
    childrenField: 'children',
    useCheckbox: true
  };
}

import {Component, OnInit, ViewChild} from '@angular/core';
import {ITreeOptions, TreeComponent, TreeModel, TreeNode} from "angular-tree-component";
import { TREE_ACTIONS, IActionMapping } from 'angular-tree-component';
import {RestService} from "../services/rest.service";
import { fromEvent } from 'rxjs';
import {debounceTime} from "rxjs/operators";

function isAllowCheck(node) {
  if (node.hasChildren) {
    if (node.getVisibleChildren().length == 0) {
      return false;
    }
    else {
      const children = node.getVisibleChildren();

      for (let i = 0; i < children.length; i++) {
        if (isAllowCheck(children[i])) {
          return true;
        }
      }

      return false;
    }
  }

  return true;
}

const actionMapping:IActionMapping = {
  mouse: {
    checkboxClick: (tree, node, $event) => {
      if (!isAllowCheck(node)) {
        $event.preventDefault();
      }
      else {
        if (node.isSelected) {
          TREE_ACTIONS.DESELECT(tree, node, $event);
        }
        else {
          TREE_ACTIONS.SELECT(tree, node, $event);
        }
      }
    }
  }
};

@Component({
  selector: 'app-protected-objects',
  templateUrl: './protected-objects.component.html',
  styleUrls: ['./protected-objects.component.scss']
})
export class ProtectedObjectsComponent implements OnInit {

  constructor(private rest: RestService) {}

  ngOnInit() {
    this.loadObjects();

    const el = document.getElementById('filter');

    const keyUps = fromEvent(el, 'keyup');

    const subscription = keyUps.pipe(debounceTime(500)).subscribe((evt: KeyboardEvent) => {
      this.filterFn((<HTMLInputElement>evt.target).value, this.tree.treeModel);
    });
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

  filterFn(value: string, treeModel: TreeModel) {
    treeModel.filterNodes((node: TreeNode) => {
      let dataWithName = "";

      if (node.data.hasOwnProperty("name")) {
        dataWithName = node.data.name + " (" + node.data.data + ")";
      }
      else {
        dataWithName = node.data.data;
      }

      return fuzzysearch(value, dataWithName);
    });
  }

  options: ITreeOptions = {
    displayField: 'data',
    childrenField: 'children',
    useCheckbox: true,
    useVirtualScroll: true,
    nodeHeight: 22,
    actionMapping
  };
}

function fuzzysearch (needle: string, haystack: string) {
  const haystackLC = haystack.toLowerCase();
  const needleLC = needle.toLowerCase();

  const hlen = haystack.length;
  const nlen = needleLC.length;

  if (nlen > hlen) {
    return false;
  }
  if (nlen === hlen) {
    return needleLC === haystackLC;
  }
  outer: for (let i = 0, j = 0; i < nlen; i++) {
    const nch = needleLC.charCodeAt(i);

    while (j < hlen) {
      if (haystackLC.charCodeAt(j++) === nch) {
        continue outer;
      }
    }
    return false;
  }
  return true;
}
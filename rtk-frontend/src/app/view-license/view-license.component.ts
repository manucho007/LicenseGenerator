import {Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {IModalDialog, IModalDialogButton, IModalDialogOptions} from "ngx-modal-dialog";
import {ITreeOptions, TreeComponent, TreeModel, TreeNode} from "angular-tree-component";
import {TREE_EVENTS} from "angular-tree-component/dist/constants/events";
import {fromEvent} from "rxjs";
import {debounceTime} from "rxjs/operators";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-view-license',
  templateUrl: './view-license.component.html',
  styleUrls: ['./view-license.component.scss']
})
export class ViewLicenseComponent implements OnInit, IModalDialog {
  actionButtons: IModalDialogButton[];

  constructor() {
    this.actionButtons = [
      { text: 'Close' }
    ];
  }

  ngOnInit() {
    const el = document.getElementById('viewLicenseFilter');

    const keyUps = fromEvent(el, 'keyup');

    const subscription = keyUps.pipe(debounceTime(500)).subscribe((evt: KeyboardEvent) => {
      this.filterFn((<HTMLInputElement>evt.target).value, this.tree.treeModel);
    });
  }

  @ViewChild(TreeComponent)
  private tree: TreeComponent;

  fillNodes(data) {
    const self = this;

    Object.keys(data).map(function (objectKeyElement) {
      const mapKeys = data[objectKeyElement];

      Object.keys(mapKeys).map(function (mapKeyElement) {
        const value = mapKeys[mapKeyElement];
        self.nodes.push(value);
      });
    });

    this.tree.treeModel.update();
  }

  beginDate;
  endDate;

  dialogInit(reference: ComponentRef<IModalDialog>, options: Partial<IModalDialogOptions<any>>) {
    let dPDate = new DatePipe(navigator.language);

    let p = 'dd.MM.yyyy';

    this.beginDate = dPDate.transform(options.data["beginDate"], p);
    this.endDate = dPDate.transform(options.data["endDate"], p);

    const protectedObjects = options.data["protectedObjects"];

    this.fillNodes(protectedObjects);
  }

  nodes = [];

  private _filterNode(ids, node, filterFn, autoShow) {
    let isVisible = filterFn(node);

    if (node.children && !isVisible) {
      node.children.forEach((child) => {
        if (this._filterNode(ids, child, filterFn, autoShow)) {
          isVisible = true;
        }
      });
    }

    if (!isVisible) {
      ids[node.id] = true;
    }

    if (autoShow && isVisible) {
      node.ensureVisible();
    }
    return isVisible;
  }

  filterFn(value: string, treeModel: TreeModel) {
    const ids = {};

    this.tree.treeModel.roots.forEach((node) => this._filterNode(ids, node, (node: TreeNode) => {
      let dataWithName = "";

      if (node.data.hasOwnProperty("name")) {
        dataWithName = node.data.name + " (" + node.data.data + ")";
      }
      else {
        dataWithName = node.data.data;
      }

      return fuzzysearch(value, dataWithName);
    }, true));

    this.tree.treeModel.hiddenNodeIds = ids;
    this.tree.treeModel.fireEvent({ eventName: TREE_EVENTS.changeFilter });
  }

  options: ITreeOptions = {
    displayField: 'data',
    childrenField: 'children',
    useCheckbox: false,
    useVirtualScroll: true,
    nodeHeight: 22
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

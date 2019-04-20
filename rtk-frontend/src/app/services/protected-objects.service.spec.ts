import { TestBed } from '@angular/core/testing';

import { ProtectedObjectsService } from './protected-objects.service';

describe('ProtectedObjectsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProtectedObjectsService = TestBed.get(ProtectedObjectsService);
    expect(service).toBeTruthy();
  });
});

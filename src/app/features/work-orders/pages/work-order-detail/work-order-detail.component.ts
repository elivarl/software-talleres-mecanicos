import { CommonModule, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { Panel } from 'primeng/panel';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { Textarea } from 'primeng/textarea';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { Customer } from '../../../../core/models/customer.model';
import {
  AddInspectionPhotoRequest,
  Inspection,
  InspectionUpsertRequest
} from '../../../../core/models/inspection.model';
import { User } from '../../../../core/models/user.model';
import { Vehicle } from '../../../../core/models/vehicle.model';
import {
  getWorkOrderStatusLabel,
  getWorkOrderStatusSeverity,
  WorkOrder,
  WorkOrderStatus
} from '../../../../core/models/work-order.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { InspectionService } from '../../../../core/services/inspection.service';
import { UserService } from '../../../../core/services/user.service';
import { VehicleService } from '../../../../core/services/vehicle.service';
import { WorkOrderService } from '../../../../core/services/work-order.service';
import { LaborSectionComponent } from '../../components/labor-section/labor-section.component';
import { QuotationSectionComponent } from '../../../quotations/components/quotation-section/quotation-section.component';
import { UsedPartsSectionComponent } from '../../components/used-parts-section/used-parts-section.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-work-order-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    PageHeaderComponent,
    LoadingStateComponent,
    EmptyStateComponent,
    Card,
    Panel,
    Tag,
    Button,
    Select,
    InputText,
    InputNumber,
    Textarea,
    TableModule,
    LaborSectionComponent,
    QuotationSectionComponent,
    UsedPartsSectionComponent
  ],
  templateUrl: './work-order-detail.component.html',
  styleUrl: './work-order-detail.component.css'
})
export class WorkOrderDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly workOrderService = inject(WorkOrderService);
  private readonly inspectionService = inject(InspectionService);
  private readonly customerService = inject(CustomerService);
  private readonly vehicleService = inject(VehicleService);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  readonly workOrder = signal<WorkOrder | null>(null);
  readonly inspection = signal<Inspection | null>(null);
  readonly customer = signal<Customer | null>(null);
  readonly vehicle = signal<Vehicle | null>(null);
  readonly mechanics = signal<User[]>([]);
  readonly loading = signal(false);
  readonly inspectionSaving = signal(false);
  readonly diagnosisSaving = signal(false);
  readonly notesSaving = signal(false);
  readonly photoSaving = signal(false);
  readonly mechanicSaving = signal(false);
  readonly currentUser = this.authService.currentUser;
  readonly currentRole = computed(() => this.currentUser()?.role ?? null);
  readonly booleanOptions = [
    { label: 'Sí', value: true },
    { label: 'No', value: false }
  ];
  readonly canManageWorkOrder = computed(
    () => this.currentRole() === 'ADMIN' || this.currentRole() === 'RECEPTIONIST'
  );
  readonly isTerminalStatus = computed(() => {
    const status = this.workOrder()?.status;
    return status === 'DELIVERED' || status === 'CANCELLED' || status === 'REJECTED';
  });
  readonly canStartDiagnosis = computed(
    () => this.canManageWorkOrder() && this.workOrder()?.status === 'RECEIVED'
  );
  readonly canEditInspection = computed(
    () => this.canManageWorkOrder() && !this.isTerminalStatus()
  );
  readonly canEditDiagnosis = computed(() => {
    const status = this.workOrder()?.status;
    return status === 'RECEIVED' || status === 'DIAGNOSIS';
  });
  readonly canEditInternalNotes = computed(() => !this.isTerminalStatus());
  readonly canAssignMechanic = computed(
    () => this.currentRole() === 'ADMIN' && !this.isTerminalStatus()
  );
  readonly headerTitle = computed(() => {
    const workOrder = this.workOrder();
    return workOrder ? `Orden ${workOrder.code}` : 'Orden de trabajo';
  });
  readonly headerSubtitle = computed(() => {
    const workOrder = this.workOrder();

    if (!workOrder) {
      return 'Pantalla central para recepción, inspección y diagnóstico.';
    }

    return `Estado actual: ${this.getStatusLabel(workOrder.status)}.`;
  });

  readonly assignMechanicForm = this.formBuilder.group({
    assignedMechanicId: [null as number | null, [Validators.required]]
  });

  readonly diagnosisForm = this.formBuilder.group({
    diagnosis: ['', [Validators.required]]
  });

  readonly internalNotesForm = this.formBuilder.group({
    internalNotes: ['', [Validators.required]]
  });

  readonly inspectionForm = this.formBuilder.group({
    mileage: [null as number | null, [Validators.required, Validators.min(0)]],
    fuelLevel: ['', [Validators.maxLength(50)]],
    exteriorCondition: ['', [Validators.maxLength(255)]],
    visibleScratches: [''],
    visibleDents: [''],
    lightsWorking: [null as boolean | null],
    tiresCondition: ['', [Validators.maxLength(255)]],
    mirrorsCondition: ['', [Validators.maxLength(255)]],
    hasSpareTire: [null as boolean | null],
    hasJack: [null as boolean | null],
    hasTools: [null as boolean | null],
    hasDocuments: [null as boolean | null],
    personalItemsNotes: [''],
    generalNotes: ['']
  });

  readonly photoForm = this.formBuilder.group({
    photoUrl: ['', [Validators.required, Validators.maxLength(500), Validators.pattern(/^https?:\/\/.+/i)]],
    description: ['', [Validators.maxLength(255)]]
  });

  ngOnInit(): void {
    const workOrderId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadWorkOrder(workOrderId);
  }

  backToList(): void {
    void this.router.navigate(['/work-orders']);
  }

  goToCustomer(): void {
    const customerId = this.workOrder()?.customerId;

    if (customerId) {
      void this.router.navigate(['/customers', customerId]);
    }
  }

  goToVehicle(): void {
    const vehicleId = this.workOrder()?.vehicleId;

    if (vehicleId) {
      void this.router.navigate(['/vehicles', vehicleId]);
    }
  }

  startDiagnosis(): void {
    const workOrder = this.workOrder();

    if (!workOrder) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Iniciar diagnóstico',
      message:
        'La orden cambiará a estado Diagnóstico. Usa esta acción solo cuando la recepción inicial ya esté revisada.',
      acceptLabel: 'Continuar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'primary' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.updateStatus(workOrder.id, 'DIAGNOSIS')
    });
  }

  saveDiagnosis(): void {
    if (this.diagnosisForm.invalid || !this.workOrder()) {
      this.diagnosisForm.markAllAsTouched();
      return;
    }

    this.diagnosisSaving.set(true);
    this.workOrderService
      .updateDiagnosis(this.workOrder()!.id, {
        diagnosis: this.diagnosisForm.getRawValue().diagnosis?.trim() || ''
      })
      .pipe(finalize(() => this.diagnosisSaving.set(false)))
      .subscribe({
        next: (workOrder) => {
          this.workOrder.set(workOrder);
          this.patchWorkOrderForms(workOrder);
          this.messageService.add({
            severity: 'success',
            summary: 'Diagnóstico',
            detail: 'Diagnóstico registrado correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Diagnóstico',
            detail: (error.error?.message as string) || 'No se pudo registrar el diagnóstico.'
          });
        }
      });
  }

  saveInternalNotes(): void {
    if (this.internalNotesForm.invalid || !this.workOrder()) {
      this.internalNotesForm.markAllAsTouched();
      return;
    }

    this.notesSaving.set(true);
    this.workOrderService
      .updateInternalNotes(this.workOrder()!.id, {
        internalNotes: this.internalNotesForm.getRawValue().internalNotes?.trim() || ''
      })
      .pipe(finalize(() => this.notesSaving.set(false)))
      .subscribe({
        next: (workOrder) => {
          this.workOrder.set(workOrder);
          this.patchWorkOrderForms(workOrder);
          this.messageService.add({
            severity: 'success',
            summary: 'Notas internas',
            detail: 'Notas internas actualizadas correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Notas internas',
            detail: (error.error?.message as string) || 'No se pudieron actualizar las notas.'
          });
        }
      });
  }

  saveInspection(): void {
    if (this.inspectionForm.invalid || !this.workOrder()) {
      this.inspectionForm.markAllAsTouched();
      return;
    }

    this.inspectionSaving.set(true);

    const request$ = this.inspection()
      ? this.inspectionService.updateInspection(this.inspection()!.id, this.buildInspectionPayload())
      : this.inspectionService.createInspection(this.workOrder()!.id, this.buildInspectionPayload());

    request$
      .pipe(finalize(() => this.inspectionSaving.set(false)))
      .subscribe({
        next: (inspection) => {
          const hadInspection = this.inspection() !== null;
          this.inspection.set(inspection);
          this.patchInspectionForm(inspection);
          this.messageService.add({
            severity: 'success',
            summary: 'Inspección inicial',
            detail: hadInspection
              ? 'Inspección actualizada correctamente.'
              : 'Inspección registrada correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Inspección inicial',
            detail: (error.error?.message as string) || 'No se pudo guardar la inspección.'
          });
        }
      });
  }

  addPhoto(): void {
    if (this.photoForm.invalid || !this.inspection()) {
      this.photoForm.markAllAsTouched();
      return;
    }

    this.photoSaving.set(true);
    this.inspectionService
      .addPhoto(this.inspection()!.id, this.buildPhotoPayload())
      .pipe(finalize(() => this.photoSaving.set(false)))
      .subscribe({
        next: (inspection) => {
          this.inspection.set(inspection);
          this.photoForm.reset({ photoUrl: '', description: '' });
          this.messageService.add({
            severity: 'success',
            summary: 'Fotos de inspección',
            detail: 'Foto agregada correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Fotos de inspección',
            detail: (error.error?.message as string) || 'No se pudo agregar la foto.'
          });
        }
      });
  }

  assignMechanic(): void {
    if (this.assignMechanicForm.invalid || !this.workOrder()) {
      this.assignMechanicForm.markAllAsTouched();
      return;
    }

    this.mechanicSaving.set(true);
    this.workOrderService
      .assignMechanic(this.workOrder()!.id, {
        assignedMechanicId: this.assignMechanicForm.getRawValue().assignedMechanicId!
      })
      .pipe(finalize(() => this.mechanicSaving.set(false)))
      .subscribe({
        next: (workOrder) => {
          this.workOrder.set(workOrder);
          this.patchWorkOrderForms(workOrder);
          this.messageService.add({
            severity: 'success',
            summary: 'Órdenes de trabajo',
            detail: 'Mecánico asignado correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Órdenes de trabajo',
            detail: (error.error?.message as string) || 'No se pudo asignar el mecánico.'
          });
        }
      });
  }

  hasInspectionError(controlName: keyof typeof this.inspectionForm.controls, errorCode: string): boolean {
    const control = this.inspectionForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  hasDiagnosisError(errorCode: string): boolean {
    const control = this.diagnosisForm.controls.diagnosis;
    return control.touched && control.hasError(errorCode);
  }

  hasNotesError(errorCode: string): boolean {
    const control = this.internalNotesForm.controls.internalNotes;
    return control.touched && control.hasError(errorCode);
  }

  hasPhotoError(controlName: keyof typeof this.photoForm.controls, errorCode: string): boolean {
    const control = this.photoForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  getStatusLabel(status: WorkOrderStatus): string {
    return getWorkOrderStatusLabel(status);
  }

  getStatusSeverity(status: WorkOrderStatus) {
    return getWorkOrderStatusSeverity(status);
  }

  private loadWorkOrder(workOrderId: number): void {
    this.loading.set(true);

    this.workOrderService.getWorkOrderById(workOrderId).subscribe({
      next: (workOrder) => {
        this.workOrder.set(workOrder);
        this.patchWorkOrderForms(workOrder);
        this.loadCustomer(workOrder.customerId);
        this.loadVehicle(workOrder.vehicleId);
        this.loadInspection(workOrder.id);
        this.loadMechanics(workOrder.assignedMechanicId);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Órdenes de trabajo',
          detail:
            (error.error?.message as string) || 'No se pudo cargar el detalle de la orden.'
        });
        void this.router.navigate(['/work-orders']);
      }
    });
  }

  private loadCustomer(customerId: number): void {
    this.customerService.getCustomerById(customerId).subscribe({
      next: (customer) => this.customer.set(customer),
      error: () => {
        this.customer.set(null);
      }
    });
  }

  private loadVehicle(vehicleId: number): void {
    this.vehicleService.getVehicleById(vehicleId).subscribe({
      next: (vehicle) => this.vehicle.set(vehicle),
      error: () => {
        this.vehicle.set(null);
      }
    });
  }

  private loadInspection(workOrderId: number): void {
    this.inspectionService.getInspectionByWorkOrderId(workOrderId).subscribe({
      next: (inspection) => {
        this.inspection.set(inspection);
        this.patchInspectionForm(inspection);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          this.inspection.set(null);
          return;
        }

        this.messageService.add({
          severity: 'warn',
          summary: 'Inspección inicial',
          detail: 'No se pudo cargar la inspección inicial de esta orden.'
        });
      }
    });
  }

  private loadMechanics(assignedMechanicId?: number): void {
    if (!this.canAssignMechanic()) {
      return;
    }

    this.userService.listUsers().subscribe({
      next: (users) => {
        const mechanics = users.filter((user) => user.role === 'MECHANIC' && user.active !== false);
        this.mechanics.set(mechanics);

        if (assignedMechanicId) {
          this.assignMechanicForm.patchValue({
            assignedMechanicId
          });
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'warn',
          summary: 'Órdenes de trabajo',
          detail: 'No se pudo cargar la lista de mecánicos.'
        });
      }
    });
  }

  private updateStatus(workOrderId: number, status: WorkOrderStatus): void {
    this.workOrderService.updateStatus(workOrderId, { status }).subscribe({
      next: (workOrder) => {
        this.workOrder.set(workOrder);
        this.patchWorkOrderForms(workOrder);
        this.messageService.add({
          severity: 'success',
          summary: 'Órdenes de trabajo',
          detail: 'Estado actualizado correctamente.'
        });
      },
      error: (error: HttpErrorResponse) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Órdenes de trabajo',
          detail: (error.error?.message as string) || 'No se pudo actualizar el estado.'
        });
      }
    });
  }

  private patchWorkOrderForms(workOrder: WorkOrder): void {
    this.diagnosisForm.patchValue({
      diagnosis: workOrder.diagnosis || ''
    });

    this.internalNotesForm.patchValue({
      internalNotes: workOrder.internalNotes || ''
    });

    this.assignMechanicForm.patchValue({
      assignedMechanicId: workOrder.assignedMechanicId ?? null
    });
  }

  private patchInspectionForm(inspection: Inspection): void {
    this.inspectionForm.patchValue({
      mileage: inspection.mileage,
      fuelLevel: inspection.fuelLevel || '',
      exteriorCondition: inspection.exteriorCondition || '',
      visibleScratches: inspection.visibleScratches || '',
      visibleDents: inspection.visibleDents || '',
      lightsWorking: inspection.lightsWorking ?? null,
      tiresCondition: inspection.tiresCondition || '',
      mirrorsCondition: inspection.mirrorsCondition || '',
      hasSpareTire: inspection.hasSpareTire ?? null,
      hasJack: inspection.hasJack ?? null,
      hasTools: inspection.hasTools ?? null,
      hasDocuments: inspection.hasDocuments ?? null,
      personalItemsNotes: inspection.personalItemsNotes || '',
      generalNotes: inspection.generalNotes || ''
    });
  }

  private buildInspectionPayload(): InspectionUpsertRequest {
    const rawValue = this.inspectionForm.getRawValue();

    return {
      mileage: rawValue.mileage!,
      fuelLevel: rawValue.fuelLevel?.trim() || undefined,
      exteriorCondition: rawValue.exteriorCondition?.trim() || undefined,
      visibleScratches: rawValue.visibleScratches?.trim() || undefined,
      visibleDents: rawValue.visibleDents?.trim() || undefined,
      lightsWorking: rawValue.lightsWorking ?? undefined,
      tiresCondition: rawValue.tiresCondition?.trim() || undefined,
      mirrorsCondition: rawValue.mirrorsCondition?.trim() || undefined,
      hasSpareTire: rawValue.hasSpareTire ?? undefined,
      hasJack: rawValue.hasJack ?? undefined,
      hasTools: rawValue.hasTools ?? undefined,
      hasDocuments: rawValue.hasDocuments ?? undefined,
      personalItemsNotes: rawValue.personalItemsNotes?.trim() || undefined,
      generalNotes: rawValue.generalNotes?.trim() || undefined
    };
  }

  private buildPhotoPayload(): AddInspectionPhotoRequest {
    const rawValue = this.photoForm.getRawValue();

    return {
      photoUrl: rawValue.photoUrl?.trim() || '',
      description: rawValue.description?.trim() || undefined
    };
  }
}

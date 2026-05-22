export interface StatCardModel {
  label: string;
  value: string;
  helper: string;
  icon: string;
  severity?: 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast';
}

import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-csv-import',
  standalone: true,
  templateUrl: './csv-import.component.html',
  styleUrl: './csv-import.component.css'
})
export class CsvImportComponent {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);

  selectedFile: File | null = null;
  uploading = false;
  result: any = null;
  error: string | null = null;

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && file.name.endsWith('.csv')) {
      this.selectedFile = file;
      this.error = null;
    } else {
      this.error = 'Please select a valid .csv file';
      this.selectedFile = null;
    }
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.error = 'Please select a file first';
      return;
    }

    this.uploading = true;
    this.result = null;
    this.error = null;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post<any>('http://localhost:8080/import/players/csv', formData).subscribe({
      next: (response) => {
        this.result = response;
        this.uploading = false;
        this.selectedFile = null;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Import failed', err);
        this.error = 'Import failed. Please check your file and try again.';
        this.uploading = false;
        this.cdr.markForCheck();
      }
    });
  }

  downloadTemplate(): void {
    const header = 'name,age,position,team,matchesPlayed,goals,assists,minutesPlayed,yellowCards,redCards,shotsOnTarget,passAccuracy,formRating,injuryStatus';
    const example = 'Bukayo Saka,22,Winger,Arsenal,32,14,11,2650,4,0,26,84.7,88.5,false';
    const csv = header + '\n' + example;
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'players-template.csv';
    a.click();
    window.URL.revokeObjectURL(url);
  }
}

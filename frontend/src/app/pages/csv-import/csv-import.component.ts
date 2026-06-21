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
    const header =
      'name,age,position,team,matchesPlayed,goals,assists,minutesPlayed,yellowCards,redCards,shotsOnTarget,passAccuracy,formRating,injuryStatus,expectedGoals,expectedAssists,keyPasses,progressivePasses,dribblesCompleted,tacklesWon,interceptions,ballRecoveries,matchesMissed,recentMatchLoad';

    const rows = [
      'Bukayo Saka,22,Winger,Arsenal,32,14,11,2650,4,0,26,84.7,88.5,false,12.8,9.4,58,132,47,18,12,76,2,6',
      '"Kevin De Bruyne",33,Midfielder,"Manchester City",24,6,15,1890,2,0,18,89.4,91.2,false,7.1,13.8,84,210,19,10,16,59,4,5',
      '"Virgil van Dijk",32,Defender,Liverpool,34,3,2,3060,5,0,7,91.1,87.0,false,2.4,1.1,12,88,6,42,39,121,1,4',
      'Pedri,21,Midfielder,Barcelona,20,4,6,1540,3,0,10,90.2,85.6,true,3.8,5.7,41,126,28,15,19,64,8,6',
      '"Erling Haaland",23,Striker,"Manchester City",30,27,5,2400,3,1,64,72.3,94.1,false,25.6,4.2,18,44,21,5,4,23,2,5'
    ];

    const csv = [header, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'players-statsbomb-template.csv';
    a.click();

    window.URL.revokeObjectURL(url);
  }
}

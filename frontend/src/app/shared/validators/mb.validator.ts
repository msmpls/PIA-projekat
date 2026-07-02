import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function pibValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    
    // Provera: 8 cifara i ne počinje nulom
    const mbRegex = /^[1-9]\d{7}$/;

    if (!value) return null; // Ako je polje prazno, 'required' validator će to uhvatiti

    const valid = mbRegex.test(value);
    return valid ? null : { invalidMb: true };
  };
}
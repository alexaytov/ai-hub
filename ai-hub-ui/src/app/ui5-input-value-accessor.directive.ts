import { Directive, ElementRef, Renderer2 } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Directive({
  selector:
    'ui5-input[formControlName], ui5-input[formControl], ui5-input[ngModel]',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: Ui5InputValueAccessorDirective,
      multi: true,
    },
  ],
})
export class Ui5InputValueAccessorDirective implements ControlValueAccessor {
  private onChange = (_: any) => {};
  private onTouched = () => {};

  constructor(private el: ElementRef, private renderer: Renderer2) {
    const element = this.el.nativeElement;

    element.addEventListener('input', (event: any) => {
      this.onChange(event.target.value);
    });

    element.addEventListener('blur', () => {
      this.onTouched();
    });
  }

  writeValue(value: any): void {
    this.renderer.setProperty(this.el.nativeElement, 'value', value);
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.renderer.setProperty(this.el.nativeElement, 'disabled', isDisabled);
  }
}

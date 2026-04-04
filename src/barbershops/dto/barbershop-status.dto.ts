import { IsBoolean } from 'class-validator';

export class BarbershopStatusDto {
  @IsBoolean()
  isActive!: boolean;
}

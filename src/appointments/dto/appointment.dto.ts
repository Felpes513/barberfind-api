import {
  IsDateString,
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
} from 'class-validator';

export class AppointmentCreateDto {
  @IsString()
  @IsNotEmpty()
  barbershopId!: string;

  @IsString()
  @IsNotEmpty()
  barberId!: string;

  @IsString()
  @IsNotEmpty()
  serviceId!: string;

  @IsDateString()
  scheduledAt!: string;

  @IsOptional()
  @IsString()
  paymentMethod?: string;
}

export class AppointmentCancelDto {
  @IsOptional()
  @IsString()
  cancellationReason?: string;
}

export class AppointmentCompleteDto {
  @IsOptional()
  @IsNumber()
  finalPrice?: number;

  @IsOptional()
  @IsString()
  paymentMethod?: string;
}

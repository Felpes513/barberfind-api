import {
  IsDateString,
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class AppointmentCreateDto {
  @ApiProperty({ example: 'clx123-barbershop-id' })
  @IsString()
  @IsNotEmpty()
  barbershopId!: string;

  @ApiProperty({ example: 'clx123-barber-id' })
  @IsString()
  @IsNotEmpty()
  barberId!: string;

  @ApiProperty({ example: 'clx123-service-id' })
  @IsString()
  @IsNotEmpty()
  serviceId!: string;

  @ApiProperty({ example: '2026-04-08T14:30:00.000Z' })
  @IsDateString()
  scheduledAt!: string;

  @ApiPropertyOptional({ example: 'PIX' })
  @IsOptional()
  @IsString()
  paymentMethod?: string;
}

export class AppointmentCancelDto {
  @ApiPropertyOptional({ example: 'Cliente avisou com antecedencia' })
  @IsOptional()
  @IsString()
  cancellationReason?: string;
}

export class AppointmentCompleteDto {
  @ApiPropertyOptional({ example: 65.5 })
  @IsOptional()
  @IsNumber()
  finalPrice?: number;

  @ApiPropertyOptional({ example: 'CREDIT' })
  @IsOptional()
  @IsString()
  paymentMethod?: string;
}

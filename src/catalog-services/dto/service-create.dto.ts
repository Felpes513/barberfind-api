import {
  IsInt,
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
  Min,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class ServiceCreateDto {
  @ApiProperty({ example: 'Corte social' })
  @IsString()
  @IsNotEmpty()
  name!: string;

  @ApiPropertyOptional({
    example: 'Corte tradicional com acabamento em maquina.',
  })
  @IsOptional()
  @IsString()
  description?: string;

  @ApiProperty({ example: 45 })
  @IsNumber()
  @Min(0.0001)
  basePrice!: number;

  @ApiProperty({ example: 40 })
  @IsInt()
  @Min(1)
  durationMinutes!: number;
}

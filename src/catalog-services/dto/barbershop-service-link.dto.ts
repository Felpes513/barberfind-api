import {
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
  Min,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class BarbershopServiceLinkDto {
  @ApiProperty({ example: 'clx123-service-id' })
  @IsString()
  @IsNotEmpty()
  serviceId!: string;

  @ApiPropertyOptional({ example: 55 })
  @IsOptional()
  @IsNumber()
  @Min(0)
  customPrice?: number;
}

export class BarbershopServiceUpdateDto {
  @ApiProperty({ example: 60 })
  @IsNumber()
  @Min(0)
  customPrice!: number;
}

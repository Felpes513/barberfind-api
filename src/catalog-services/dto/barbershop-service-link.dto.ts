import { IsNotEmpty, IsNumber, IsOptional, IsString, Min } from 'class-validator';

export class BarbershopServiceLinkDto {
  @IsString()
  @IsNotEmpty()
  serviceId!: string;

  @IsOptional()
  @IsNumber()
  @Min(0)
  customPrice?: number;
}

export class BarbershopServiceUpdateDto {
  @IsNumber()
  @Min(0)
  customPrice!: number;
}

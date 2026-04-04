import { IsNotEmpty, IsString } from 'class-validator';

export class PhotoAddDto {
  @IsString()
  @IsNotEmpty()
  imageData!: string;

  @IsString()
  @IsNotEmpty()
  mediaType!: string;
}

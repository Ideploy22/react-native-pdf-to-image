import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'rn-pdt-to-image' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RnPdtToImage = NativeModules.RnPdtToImage
  ? NativeModules.RnPdtToImage
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

type Props = {
  files: string[] | undefined;
};

export function onPdtToImages(base64: string, dpi: number): Promise<Props> {
  return RnPdtToImage.onPdtToImages(base64, dpi);
}

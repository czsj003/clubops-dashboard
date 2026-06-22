import axios from "axios";

export function getApiErrorMessage(
  error: unknown,
  fallback = "Something went wrong. Please try again."
) {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }

    if (typeof error.response?.data === "string") {
      return error.response.data;
    }
  }

  return fallback;
}

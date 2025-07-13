import json
import os
import uuid

def safe_wrap_outliner_elements_bbmodel():
    """
    Processes all .bbmodel files in the current working directory.
    It detects if the 'outliner' key has already been wrapped into the
    '_all_model' structure and skips the file if it has. Otherwise,
    it performs the wrapping conversion.
    """
    current_working_directory = os.getcwd()
    print(f"Searching for .bbmodel files in: {current_working_directory}")

    found_files = False
    for filename in os.listdir(current_working_directory):
        if filename.endswith(".bbmodel"):
            found_files = True
            file_path = os.path.join(current_working_directory, filename)
            print(f"Processing file: {file_path}")

            try:
                with open(file_path, 'r+', encoding='utf-8') as f:
                    data = json.load(f)

                    # --- Detection Logic: Check if it's already converted ---
                    is_already_converted = False
                    if (
                        "outliner" in data and
                        isinstance(data["outliner"], list) and
                        len(data["outliner"]) == 1 and
                        isinstance(data["outliner"][0], dict) and
                        data["outliner"][0].get("name") == "_all_model" and
                        "children" in data["outliner"][0]
                    ):
                        is_already_converted = True

                    if is_already_converted:
                        print(f"Skipping '{filename}': Already appears to be converted.")
                        continue # Move to the next file
                    # --- End Detection Logic ---

                    # If not already converted, proceed with the conversion
                    if "outliner" in data and isinstance(data["outliner"], list):
                        original_outliner_elements = data["outliner"]

                        # Create the new wrapping structure
                        wrapped_structure = {
                            "name": "_all_model",
                            "origin": [0, 0, 0],
                            "color": 0,
                            "uuid": str(uuid.uuid4()),  # Generate a random dashed UUID
                            "export": True,
                            "isOpen": True,
                            "locked": False,
                            "visibility": True,
                            "autouv": 0,
                            "children": original_outliner_elements
                        }

                        # Replace the original outliner list with the new wrapped structure
                        data["outliner"] = [wrapped_structure]

                        # Go to the beginning of the file to overwrite it
                        f.seek(0)
                        json.dump(data, f, indent=4, ensure_ascii=False)
                        f.truncate() # Truncate any remaining old content if the new content is smaller
                        print(f"Successfully updated '{filename}'.")
                    else:
                        print(f"Skipping '{filename}': 'outliner' key not found or is not a list (and not yet converted).")

            except json.JSONDecodeError:
                print(f"Error: Could not decode JSON from '{filename}'. Skipping.")
            except Exception as e:
                print(f"An unexpected error occurred while processing '{filename}': {e}")

    if not found_files:
        print("No .bbmodel files found in the current directory.")


# --- How to use the script ---
if __name__ == "__main__":
    print("\n--- Starting .bbmodel file conversion (with detection) ---")
    safe_wrap_outliner_elements_bbmodel()
    print("\n--- Conversion process finished. ---")